package com.example.routes

import com.example.model.ArtistRequest
import com.example.model.ArtistResponse
import com.example.model.ErrorResponse
import com.example.model.MessageResponse
import com.example.service.ArtistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.artistRoutes(artistService: ArtistService) {
    route("/artistas") {

        //  Crear
        post {
            try {
                val request = call.receive<ArtistRequest>()
                val newId = artistService.create(request.name, request.genre)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error al crear: ${e.message}"))
            }
        }

        //  Listar Todos
        get {
            try {
                val artists = artistService.getAll().map {
                    ArtistResponse(it.id.toString(), it.name, it.genre)
                }
                call.respond(HttpStatusCode.OK, artists)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error al listar: ${e.message}"))
            }
        }

        //  Obtener por ID
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val artist = artistService.getById(id)
                if (artist != null) {
                    call.respond(HttpStatusCode.OK, ArtistResponse(artist.id.toString(), artist.name, artist.genre))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
            }
        }

        // : Actualizar
        put("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val request = call.receive<ArtistRequest>()
                if (artistService.update(id, request.name, request.genre)) {
                    call.respond(HttpStatusCode.OK, MessageResponse("Actualizado correctamente"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Datos inválidos: ${e.message}"))
            }
        }

        //  DELETE
        delete("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                if (artistService.delete(id)) {
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