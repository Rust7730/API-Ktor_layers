package com.example.routes

import com.example.model.ErrorResponse
import com.example.model.LoginRequest
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {

        post("/login") {
            // 1. Recibimos el body (StatusPages maneja si el JSON está mal formado)
            val loginRequest = call.receive<LoginRequest>()

            // 2. Llamamos al servicio (que ya verifica password y genera token)
            val token = authService.login(loginRequest)

            if (token != null) {
                // 3. Éxito: Devolvemos el token
                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            } else {
                // 4. Fallo: Credenciales incorrectas
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Credenciales inválidas"))
            }
        }
    }
}