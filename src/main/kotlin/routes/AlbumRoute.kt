package com.example.routes

import com.example.model.AlbumRequest
import com.example.model.AlbumResponse
import com.example.model.ErrorResponse
import com.example.model.MessageResponse
import com.example.service.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.albumRoutes(albumService: AlbumService) {
    route("/albumes") {

        //  Crear
        post {
            try {
                val request = call.receive<AlbumRequest>()
                val artistId = UUID.fromString(request.artistId)
                val newId = albumService.create(request.title, request.releaseYear, artistId)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error: ${e.message}"))
            }
        }

        //  Listar Todos
        get {
            try {
                val albums = albumService.getAll().map {
                    AlbumResponse(it.id.toString(), it.title, it.releaseYear, it.artistId.toString())
                }
                call.respond(HttpStatusCode.OK, albums)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error: ${e.message}"))
            }
        }

        //  Obtener por ID
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val album = albumService.getById(id)
                if (album != null) {
                    call.respond(HttpStatusCode.OK, AlbumResponse(album.id.toString(), album.title, album.releaseYear, album.artistId.toString()))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
            }
        }

        //  Actualizar
        put("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val request = call.receive<AlbumRequest>()
                val artistId = UUID.fromString(request.artistId)
                if (albumService.update(id, request.title, request.releaseYear, artistId)) {
                    call.respond(HttpStatusCode.OK, MessageResponse("Actualizado"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Datos inválidos"))
            }
        }

        //  Delete
        delete("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                if (albumService.delete(id)) {
                    call.respond(HttpStatusCode.OK, MessageResponse("Eliminado"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
            }
        }
    }
}