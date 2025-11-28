package com.example.model
import java.util.UUID

data class Usuario (
    val id : UUID,
    val name: String,
    val password: String
    )