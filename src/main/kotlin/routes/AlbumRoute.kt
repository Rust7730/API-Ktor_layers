package com.example.routes

import com.example.model.AlbumRequest
import com.example.model.ErrorResponse
import com.example.model.MessageResponse
import com.example.service.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.albumRoutes(albumService: AlbumService) {
    route("/albums") {

        get {
            val albums = albumService.getAll()
            call.respond(HttpStatusCode.OK, albums)
        }

        get("/{id}") {
            val id = UUID.fromString(call.parameters["id"])
            val album = albumService.getById(id)
            if (album != null) {
                call.respond(HttpStatusCode.OK, album)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Álbum no encontrado"))
            }
        }


        authenticate("auth-jwt") {

            post {
                val request = call.receive<AlbumRequest>()
                val newId = albumService.create(request)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            }

            delete("/{id}") {
                val id = UUID.fromString(call.parameters["id"])
                if (albumService.delete(id)) {
                    call.respond(HttpStatusCode.OK, MessageResponse("Álbum eliminado"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Álbum no encontrado"))
                }
            }
        }
    }
}