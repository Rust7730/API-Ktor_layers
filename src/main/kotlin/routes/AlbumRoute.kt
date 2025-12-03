package com.example.routes

import com.example.model.AlbumRequest
import com.example.model.ErrorResponse
import com.example.service.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.albumRoutes(albumService: AlbumService) {
    route("/albumes") {

        // POST: Crear Álbum
        post {
            try {
                val request = call.receive<AlbumRequest>()

                val artistUuid = UUID.fromString(request.artistId)

                val newId = albumService.create(request.title, request.releaseYear, artistUuid)

                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error al crear álbum: ${e.message}"))
            }
        }
    }
}