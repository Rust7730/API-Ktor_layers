package com.example.routes

import com.example.model.ErrorResponse
import com.example.model.MessageResponse
import com.example.service.AlbumService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.albumRoutes(albumService: AlbumService) {
    route("/albums") {

        // GET ALL
        get {
            try {
                val albums = albumService.getAll()
                call.respond(HttpStatusCode.OK, albums)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error: ${e.message}"))
            }
        }

        // GET BY ID
        get("/{id}") {
            try {
                // Convertimos el String de la URL a UUID para buscar en BD
                val id = UUID.fromString(call.parameters["id"])
                val album = albumService.getById(id)

                if (album != null) {
                    call.respond(HttpStatusCode.OK, album)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Álbum no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Formato de ID inválido"))
            }
        }

        authenticate("auth-jwt") {

            // POST: Crear
            post {
                val principal = call.principal<JWTPrincipal>()
                if (principal?.payload?.getClaim("role")?.asString() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse("No autorizado"))
                    return@post
                }

                try {
                    var name = ""
                    var year = 0
                    var artistId: UUID? = null
                    var imageBytes: ByteArray? = null

                    val multipart = call.receiveMultipart()
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "year" -> year = part.value.toIntOrNull() ?: 0
                                    // Aquí convertimos el String que llega del form a UUID
                                    "artistId" -> artistId = try { UUID.fromString(part.value) } catch (e: Exception) { null }
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    imageBytes = part.streamProvider().readBytes()
                                }
                            }
                            else -> part.dispose()
                        }
                        part.dispose()
                    }

                    if (name.isBlank() || artistId == null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Datos incompletos o artistId inválido"))
                        return@post
                    }

                    val newId = albumService.create(name, year, artistId!!, imageBytes)

                    call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))

                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error interno: ${e.message}"))
                }
            }

            // DELETE
            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                if (principal?.payload?.getClaim("role")?.asString() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse("No autorizado"))
                    return@delete
                }

                try {
                    val id = UUID.fromString(call.parameters["id"])
                    if (albumService.delete(id)) {
                        call.respond(HttpStatusCode.OK, MessageResponse("Álbum eliminado"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                }
            }
        }
    }
}