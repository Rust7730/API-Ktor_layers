package com.example.routes

import com.example.model.ArtistRequest
import com.example.model.ArtistResponse
import com.example.model.ErrorResponse
import com.example.service.ArtistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.artistRoutes(artistService: ArtistService) {
    route("/artistas") {

        // POST: Crear Artista
        post {
            try {
                val request = call.receive<ArtistRequest>()
                val newId = artistService.create(request.name, request.genre)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error al crear artista: ${e.message}"))
            }
        }

        // GET: Obtener por ID
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val artist = artistService.getById(id)

                if (artist != null) {

                    val response = ArtistResponse(
                        id = artist.id.toString(),
                        name = artist.name,
                        genre = artist.genre
                    )
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Artista no encontrado"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error: ${e.message}"))
            }
        }
    }
}