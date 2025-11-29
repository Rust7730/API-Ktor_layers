package com.example.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.Users
import com.example.model.User
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AuthService {
    private val jwtSecret = "your-secret-key-change"
    private val jwtIssuer = "https://jwt-provider-domain/"
    private val jwtAudience = "jwt-audience"
    private val jwtExpirationMs = 86400000L // 24 horas

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
    }

    fun generateToken(username: String, role: String): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpirationMs))
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    fun authenticate(username: String, password: String): User? {
        return transaction {
            val row = Users.select { Users.username eq username }.singleOrNull()

            row?.let {
                val user = User(
                    id = it[Users.id],
                    username = it[Users.username],
                    password = it[Users.password],
                    role = it[Users.role]
                )

                if (verifyPassword(password, user.password)) {
                    user
                } else {
                    null
                }
            }
        }
    }
}