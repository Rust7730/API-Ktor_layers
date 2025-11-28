package com.example.model

import kotlinx.serialization.Serializable
import java.util.UUID

// Usuario de la base de datos
data class User(
    val id: UUID,
    val username: String,
    val password: String,
    val role: String
)

// DTOs para serialización JSON
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val username: String,
    val role: String
)

@Serializable
data class ErrorResponse(
    val error: String
)