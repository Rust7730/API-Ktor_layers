package com.example.model

import kotlinx.serialization.Serializable
import java.util.UUID

data class Artist(
    val id: UUID,
    val name: String,
    val genre: String?
)

// DTOs para la API
@Serializable
data class ArtistRequest(
    val name: String,
    val genre: String? = null
)

@Serializable
data class ArtistResponse(
    val id: String,
    val name: String,
    val genre: String? = null
)

@Serializable
data class MessageResponse(
    val message: String
)