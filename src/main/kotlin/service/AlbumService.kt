package com.example.service

import com.example.model.Album
import com.example.model.AlbumRequest
import com.example.repository.Albums
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AlbumService {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(request: AlbumRequest): UUID = dbQuery {
        Albums.insert {
            it[name] = request.name
            it[year] = request.year
            it[albumArt] = request.albumArt
            it[artistId] = UUID.fromString(request.artistId)
        }[Albums.id]
    }


    suspend fun getAll(): List<Album> = dbQuery {
        Albums.selectAll().map { toAlbum(it) }
    }

    suspend fun getById(id: UUID): Album? = dbQuery {
        Albums.select { Albums.id eq id }
            .map { toAlbum(it) }
            .singleOrNull()
    }

    suspend fun getByArtistId(artistId: UUID): List<Album> = dbQuery {
        Albums.select { Albums.artistId eq artistId }
            .map { toAlbum(it) }
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        Albums.deleteWhere { Albums.id eq id } > 0
    }

    private fun toAlbum(row: ResultRow): Album = Album(
        id = row[Albums.id].toString(),
        name = row[Albums.name],
        year = row[Albums.year],
        albumArt = row[Albums.albumArt],
        artistId = row[Albums.artistId].toString()
    )
}