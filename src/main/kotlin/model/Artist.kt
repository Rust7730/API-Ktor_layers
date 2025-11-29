package com.example.model

import kotlinx.serialization.Serializable
import java.util.UUID

// Modelo de dominio (base de datos)
data class Artist(
    val id: UUID,
    val name: String,
    val genre: String?,
    val image: String?
)

// DTOs para la API
@Serializable
data class ArtistRequest(
    val name: String,
    val genre: String? = null,
    val image: String? = null
)

@Serializable
data class ArtistResponse(
    val id: String,
    val name: String,
    val genre: String? = null,
    val image: String? = null
)

@Serializable
data class MessageResponse(
    val message: String
)