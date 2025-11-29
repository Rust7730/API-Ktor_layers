package com.example.service

import com.example.Artists
import com.example.model.Artist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ArtistService {

    // Crear un artista
    fun create(name: String, genre: String?, image: String?): UUID {
        return transaction {
            Artists.insert {
                it[Artists.name] = name
                it[Artists.genre] = genre
                it[Artists.image] = image
            } get Artists.id
        }
    }

    // Obtener todos los artistas
    fun getAll(): List<Artist> {
        return transaction {
            Artists.selectAll().map { rowToArtist(it) }
        }
    }

    // Obtener un artista por ID
    fun getById(id: UUID): Artist? {
        return transaction {
            Artists.select { Artists.id eq id }
                .map { rowToArtist(it) }
                .singleOrNull()
        }
    }

    // Actualizar un artista
    fun update(id: UUID, name: String, genre: String?, image: String?): Boolean {
        return transaction {
            Artists.update({ Artists.id eq id }) {
                it[Artists.name] = name
                it[Artists.genre] = genre
                it[Artists.image] = image
            } > 0
        }
    }

    // Eliminar un artista
    fun delete(id: UUID): Boolean {
        return transaction {
            Artists.deleteWhere { Artists.id eq id } > 0
        }
    }

    // Convertir Row a Artist
    private fun rowToArtist(row: ResultRow): Artist {
        return Artist(
            id = row[Artists.id],
            name = row[Artists.name],
            genre = row[Artists.genre],
            image = row[Artists.image]
        )
    }
}