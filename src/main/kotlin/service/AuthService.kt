package com.example.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.LoginRequest
import com.example.repository.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AuthService(
    private val secret: String,
    private val issuer: String,
    private val audience: String
) {


    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


    suspend fun login(request: LoginRequest): String? = dbQuery {

        val userRow = Users.select { Users.username eq request.username }
            .singleOrNull() ?: return@dbQuery null

        val passwordIsValid = BCrypt.verifyer().verify(
            request.password.toCharArray(),
            userRow[Users.password]
        ).verified

        if (passwordIsValid) {
            generateToken(userRow[Users.username], userRow[Users.role])
        } else {
            null
        }
    }

    private fun generateToken(username: String, role: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000)) // 24 horas
            .sign(Algorithm.HMAC256(secret))
    }
}