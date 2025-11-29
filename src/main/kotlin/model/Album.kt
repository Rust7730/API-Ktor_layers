package com.example.model

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class Album(
    val id: String,
    val name: String,
    val year: Int,
    val albumArt: String?,
    val artistId: String
)

// DTO para recibir datos en POST/PUT
@Serializable
data class AlbumRequest(
    val name: String,
    val year: Int,
    val albumArt: String? = null,
    val artistId: String // Recibimos el UUID como String desde el JSON
)