package com.example.model

import kotlinx.serialization.Serializable
import java.util.UUID

data class Album(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val artistId: UUID
)

// DTO para recibir datos (POST)
@Serializable
data class AlbumRequest(
    val title: String,
    val releaseYear: Int,
    val artistId: String
)


@Serializable
data class AlbumResponse(
    val id: String,
    val title: String,
    val releaseYear: Int,
    val artistId: String
)