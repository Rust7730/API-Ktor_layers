package com.example.service

import com.example.model.Artist
import com.example.repository.Artists
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class ArtistService(private val s3Service: S3Service) {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(name: String, genre: String?, imageBytes: ByteArray?): UUID {
        var imageKey: String? = null

        if (imageBytes != null && imageBytes.isNotEmpty()) {

            imageKey = s3Service.uploadFile("artist-$name.jpg", imageBytes, "image/jpeg")
        }

        return dbQuery {
            Artists.insert {
                it[Artists.name] = name
                it[Artists.genre] = genre
                it[Artists.image] = imageKey
            }[Artists.id]
        }
    }


    suspend fun getAll(): List<Artist> {
        val rows = dbQuery {
            Artists.selectAll().map { row ->
                Triple(
                    row,
                    row[Artists.image],
                    null
                )
            }
        }

        return rows.map { (row, key, _) ->
            rowToArtist(row, key)
        }
    }

    suspend fun getById(id: UUID): Artist? {
        val row = dbQuery {
            Artists.select { Artists.id eq id }.singleOrNull()
        } ?: return null

        return rowToArtist(row, row[Artists.image])
    }

    suspend fun update(id: UUID, name: String, genre: String?, imageBytes: ByteArray?): Boolean {
        var newImageKey: String? = null
        if (imageBytes != null && imageBytes.isNotEmpty()) {
            newImageKey = s3Service.uploadFile("artist-$name-${UUID.randomUUID()}.jpg", imageBytes, "image/jpeg")
        }

        return dbQuery {
            Artists.update({ Artists.id eq id }) {
                it[Artists.name] = name
                it[Artists.genre] = genre
                if (newImageKey != null) {
                    it[Artists.image] = newImageKey
                }
            } > 0
        }
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        Artists.deleteWhere { Artists.id eq id } > 0
    }

    private suspend fun rowToArtist(row: ResultRow, imageKey: String?): Artist {
        val signedUrl = imageKey?.let { s3Service.getPresignedUrl(it) }

        return Artist(
            id = row[Artists.id],
            name = row[Artists.name],
            genre = row[Artists.genre],
            image = signedUrl
        )
    }
}