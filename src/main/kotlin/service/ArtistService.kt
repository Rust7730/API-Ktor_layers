package com.example.service

import com.example.repository.Artists
import com.example.model.Artist
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class ArtistService {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(name: String, genre: String?, image: String?): UUID = dbQuery {
        Artists.insert {
            it[Artists.name] = name
            it[Artists.genre] = genre
            it[Artists.image] = image
        }[Artists.id]
    }

    suspend fun getAll(): List<Artist> = dbQuery {
        Artists.selectAll().map {
            Artist(it[Artists.id],
                it[Artists.name],
                it[Artists.genre],
                it[Artists.image])
        }
    }

    suspend fun getById (id: UUID): Artist? = dbQuery{
        Artists.select {Artists.id eq id }.map {
            Artist(it[Artists.id],
                it[Artists.name],
                it[Artists.genre],
                it[Artists.image])
        }.singleOrNull()
    }
    suspend fun update(id: UUID, name: String, genre: String?, image: String?): Boolean = dbQuery {
        Artists.update({ Artists.id eq id }) {
            it[Artists.name] = name
            it[Artists.genre] = genre
            it[Artists.image] = image
        } > 0
    }
    suspend fun delete(id: UUID): Boolean = dbQuery {
        Artists.deleteWhere { Artists.id eq id } > 0
    }
}