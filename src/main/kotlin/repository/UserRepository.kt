package com.example.repository

import com.example.model.Usuario
import java.util.*

class UserRepository {
    private val user = mutableListOf<Usuario>()

    fun findAll (): List<Usuario> = user
    fun findById (id: UUID): Usuario? = user.firstOrNull{it.id ==id }
    fun findByUsername(name: String): Usuario? = user.firstOrNull { it.name==name }
}