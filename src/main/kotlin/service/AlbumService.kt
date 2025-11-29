package com.example.service

import com.example.model.Album
import com.example.repository.Albums
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import java.util.*

class AlbumService(private val s3Service: S3Service) {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREAR
    suspend fun create(name: String, year: Int, artistId: UUID, imageBytes: ByteArray?): UUID {
        var imageKey: String? = null

        if (imageBytes != null && imageBytes.isNotEmpty()) {

            imageKey = s3Service.uploadFile("album-$name.jpg", imageBytes, "image/jpeg")
        }

        return dbQuery {
            Albums.insert {
                it[Albums.name] = name
                it[Albums.year] = year
                it[Albums.artistId] = artistId
                it[Albums.albumArt] = imageKey
            }[Albums.id]
        }
    }

    // LISTAR
    suspend fun getAll(): List<Album> {
        val rows = dbQuery {
            Albums.selectAll().map { row ->
                Triple(row, row[Albums.albumArt], null)
            }
        }
        return rows.map { (row, key, _) -> rowToAlbum(row, key) }
    }

    // OBTENER POR ID
    suspend fun getById(id: UUID): Album? {
        val row = dbQuery {
            Albums.select { Albums.id eq id }.singleOrNull()
        } ?: return null

        return rowToAlbum(row, row[Albums.albumArt])
    }

    // OBTENER POR ARTISTA
    suspend fun getByArtistId(artistId: UUID): List<Album> {
        val rows = dbQuery {
            Albums.select { Albums.artistId eq artistId }.map { row ->
                Triple(row, row[Albums.albumArt], null)
            }
        }
        return rows.map { (row, key, _) -> rowToAlbum(row, key) }
    }

    // ACTUALIZAR
    suspend fun update(id: UUID, name: String, year: Int, artistId: UUID, imageBytes: ByteArray?): Boolean {
        var newImageKey: String? = null
        if (imageBytes != null && imageBytes.isNotEmpty()) {
            newImageKey = s3Service.uploadFile("album-$name-${UUID.randomUUID()}.jpg", imageBytes, "image/jpeg")
        }

        return dbQuery {
            Albums.update({ Albums.id eq id }) {
                it[Albums.name] = name
                it[Albums.year] = year
                it[Albums.artistId] = artistId
                if (newImageKey != null) {
                    it[Albums.albumArt] = newImageKey
                }
            } > 0
        }
    }

    // ELIMINAR
    suspend fun delete(id: UUID): Boolean = dbQuery {
        Albums.deleteWhere { Albums.id eq id } > 0
    }

    private suspend fun rowToAlbum(row: ResultRow, imageKey: String?): Album {
        val signedUrl = imageKey?.let { s3Service.getPresignedUrl(it) }

        return Album(
            id = row[Albums.id].toString(),
            name = row[Albums.name],
            year = row[Albums.year],
            albumArt = signedUrl,
            artistId = row[Albums.artistId].toString()
        )
    }
}