package com.example.service

import com.example.model.Album
import com.example.repository.Albums
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AlbumService {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREAR
    suspend fun create(title: String, releaseYear: Int, artistId: UUID): UUID {
        return dbQuery {
            Albums.insert {
                it[Albums.title] = title
                it[Albums.releaseYear] = releaseYear
                it[Albums.artistId] = artistId
            }[Albums.id]
        }
    }

    // LISTAR
    suspend fun getAll(): List<Album> {
        return dbQuery {
            Albums.selectAll().map { rowToAlbum(it) }
        }
    }

    // OBTENER POR ID
    suspend fun getById(id: UUID): Album? {
        return dbQuery {
            Albums.select { Albums.id eq id }
                .map { rowToAlbum(it) }
                .singleOrNull()
        }
    }

    // OBTENER POR ARTISTA
    suspend fun getByArtistId(artistId: UUID): List<Album> {
        return dbQuery {
            Albums.select { Albums.artistId eq artistId }
                .map { rowToAlbum(it) }
        }
    }

    // ACTUALIZAR
    suspend fun update(id: UUID, title: String, releaseYear: Int, artistId: UUID): Boolean {
        return dbQuery {
            Albums.update({ Albums.id eq id }) {
                it[Albums.title] = title
                it[Albums.releaseYear] = releaseYear
                it[Albums.artistId] = artistId
            } > 0
        }
    }

    // ELIMINAR
    suspend fun delete(id: UUID): Boolean = dbQuery {
        Albums.deleteWhere { Albums.id eq id } > 0
    }

    private fun rowToAlbum(row: ResultRow): Album {
        return Album(
            id = row[Albums.id],
            title = row[Albums.title],
            releaseYear = row[Albums.releaseYear],
            artistId = row[Albums.artistId]
        )
    }
}