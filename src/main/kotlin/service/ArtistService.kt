package com.example.service

import com.example.model.Artist
import com.example.repository.Artists
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class ArtistService {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREAR
    suspend fun create(name: String, genre: String?): UUID = dbQuery {
        Artists.insert {
            it[Artists.name] = name
            it[Artists.genre] = genre
        }[Artists.id]
    }

    // LISTAR
    suspend fun getAll(): List<Artist> {
        return dbQuery {
            Artists.selectAll().map { rowToArtist(it) }
        }
    }

    // OBTENER POR ID
    suspend fun getById(id: UUID): Artist? {
        return dbQuery {
            Artists.select { Artists.id eq id }
                .map { rowToArtist(it) }
                .singleOrNull()
        }
    }

    // ACTUALIZAR
    suspend fun update(id: UUID, name: String, genre: String?): Boolean {
        return dbQuery {
            Artists.update({ Artists.id eq id }) {
                it[Artists.name] = name
                it[Artists.genre] = genre
            } > 0
        }
    }

    // ELIMINAR
    suspend fun delete(id: UUID): Boolean = dbQuery {
        Artists.deleteWhere { Artists.id eq id } > 0
    }

    private fun rowToArtist(row: ResultRow): Artist {
        return Artist(
            id = row[Artists.id],
            name = row[Artists.name],
            genre = row[Artists.genre]
        )
    }
}