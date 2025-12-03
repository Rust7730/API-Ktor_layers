package com.example.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Artist(
    val id: String,
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