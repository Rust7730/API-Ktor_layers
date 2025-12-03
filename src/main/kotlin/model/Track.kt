package com.example.model

import kotlinx.serialization.Serializable
import java.util.UUID

data class Track(
    val id: UUID,
    val title: String,
    val duration: Int,
    val albumId: UUID
)

@Serializable
data class TrackRequest(
    val title: String,
    val duration: Int,
    val albumId: String
)

@Serializable
data class TrackResponse(
    val id: String,
    val title: String,
    val duration: Int,
    val albumId: String
)