package com.example

import com.example.plugins.configureDatabase
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.routes.albumRoutes
import com.example.routes.artistRoutes
import com.example.routes.authRoutes
import com.example.service.AlbumService
import com.example.service.ArtistService
import com.example.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import jdk.internal.vm.ScopedValueContainer.call

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureDatabase()
    configureSecurity()
    configureSerialization()
    val secret = "your-secret-key-change-in-production-min-256-bits-long" // O leer de environment
    val issuer = "https://jwt-provider-domain/"
    val audience = "jwt-audience"

    //Services
    val authService = AuthService(secret, issuer, audience)
    val artistService = ArtistService()
    val albumService = AlbumService()

    routing {

        authRoutes(authService)
        artistRoutes(artistService)
        albumRoutes(albumService)

        get("/") {
            call.respondText("API Corriendo Correctamente")
        }
    }
}
