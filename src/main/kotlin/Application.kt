package com.example

import com.example.plugins.*
import com.example.routes.*
import com.example.service.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.routing

fun main(args: Array<String>) = EngineMain.main(args)
fun Application.module() {
    configureDatabase()
    configureSecurity()
    configureSerialization()


    val s3Service = S3Service(environment.config)

    val authService = AuthService(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.domain").getString(),
        audience = environment.config.property("jwt.audience").getString()
    )

    val artistService = ArtistService(s3Service)
    val albumService = AlbumService(s3Service)

    routing {
        authRoutes(authService)
        artistRoutes(artistService)
        albumRoutes(albumService)

    }
}