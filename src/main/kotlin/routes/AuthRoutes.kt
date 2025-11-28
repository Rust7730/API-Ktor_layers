package com.example.routes

import com.example.model.ErrorResponse
import com.example.model.LoginRequest
import com.example.model.LoginResponse
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    val authService = AuthService()

    route("/auth") {
        post("/login") {
            try {
                val loginRequest = call.receive<LoginRequest>()

                val user = authService.authenticate(loginRequest.username, loginRequest.password)

                if (user != null) {
                    val token = authService.generateToken(user.username, user.role)
                    call.respond(
                        HttpStatusCode.OK,
                        LoginResponse(
                            token = token,
                            username = user.username,
                            role = user.role
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Credenciales inválidas")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Error en la solicitud: ${e.message}")
                )
            }
        }
    }
}
