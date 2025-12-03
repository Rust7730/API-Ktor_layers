package com.example.service

import com.example.repository.Tracks
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class TrackService {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREAR
    suspend fun create(title: String, duration: Int, albumId: UUID): UUID {
        return dbQuery {
            Tracks.insert {
                it[Tracks.title] = title
                it[Tracks.duration] = duration
                it[Tracks.albumId] = albumId
            }[Tracks.id]
        }
    }


}