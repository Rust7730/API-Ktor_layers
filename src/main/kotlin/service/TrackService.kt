package com.example.service

import com.example.model.Track
import com.example.repository.Tracks
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class TrackService {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREAR
    suspend fun create(title: String, duration: Int, albumId: UUID): UUID = dbQuery {
        Tracks.insert {
            it[Tracks.title] = title
            it[Tracks.duration] = duration
            it[Tracks.albumId] = albumId
        }[Tracks.id]
    }

    // LISTAR
    suspend fun getAll(): List<Track> = dbQuery {
        Tracks.selectAll().map { rowToTrack(it) }
    }

    // LEER UNO
    suspend fun getById(id: UUID): Track? = dbQuery {
        Tracks.select { Tracks.id eq id }
            .map { rowToTrack(it) }
            .singleOrNull()
    }

    // ACTUALIZAR
    suspend fun update(id: UUID, title: String, duration: Int, albumId: UUID): Boolean = dbQuery {
        Tracks.update({ Tracks.id eq id }) {
            it[Tracks.title] = title
            it[Tracks.duration] = duration
            it[Tracks.albumId] = albumId
        } > 0
    }

    // ELIMINAR
    suspend fun delete(id: UUID): Boolean = dbQuery {
        Tracks.deleteWhere { Tracks.id eq id } > 0
    }

    private fun rowToTrack(row: ResultRow): Track {
        return Track(
            id = row[Tracks.id],
            title = row[Tracks.title],
            duration = row[Tracks.duration],
            albumId = row[Tracks.albumId]
        )
    }
}