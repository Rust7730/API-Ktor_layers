package com.example

import com.example.routes.artistRoutes
import com.example.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("MusicApp API - Backend running")
        }

        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }

        authRoutes()
        artistRoutes()
    }
}