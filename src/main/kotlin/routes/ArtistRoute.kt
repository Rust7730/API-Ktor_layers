package com.example.routes

import com.example.model.ArtistResponse
import com.example.model.ErrorResponse
import com.example.model.MessageResponse
import com.example.service.ArtistService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.artistRoutes(artistService: ArtistService) {
    route("/artists") {

        // GET
        get {
            try {
                val artists = artistService.getAll()
                call.respond(HttpStatusCode.OK, artists)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error al listar: ${e.message}"))
            }
        }

        // GET id
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val artist = artistService.getById(id)

                if (artist != null) {
                    call.respond(HttpStatusCode.OK, artist)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Artista no encontrado"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
            }
        }

        // Rutas Protegidas (POST, PUT, DELETE)
        authenticate("auth-jwt") {

            // POST: Crear
            post {
                //  Validar Rol
                val principal = call.principal<JWTPrincipal>()
                if (principal?.payload?.getClaim("role")?.asString() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse("No autorizado"))
                    return@post
                }

                try {
                    //  Variables para recibir datos
                    var name = ""
                    var genre = ""
                    var imageBytes: ByteArray? = null

                    //  Procesar Multipart
                    val multipart = call.receiveMultipart()
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "genre" -> genre = part.value
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

                    //  Validar y Crear
                    if (name.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Nombre es obligatorio"))
                        return@post
                    }

                    val newId = artistService.create(name, genre, imageBytes)

                    call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))

                } catch (e: Exception) {
                    e.printStackTrace() // Útil para ver errores en consola
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error interno: ${e.message}"))
                }
            }

            // PUT: Actualizar
            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                if (principal?.payload?.getClaim("role")?.asString() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse("No autorizado"))
                    return@put
                }

                try {
                    val id = UUID.fromString(call.parameters["id"])

                    var name = ""
                    var genre = ""
                    var imageBytes: ByteArray? = null

                    val multipart = call.receiveMultipart()
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "genre" -> genre = part.value
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

                    val updated = artistService.update(id, name, genre, imageBytes)

                    if (updated) {
                        call.respond(HttpStatusCode.OK, MessageResponse("Artista actualizado"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                    }

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error al actualizar"))
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
                    if (artistService.delete(id)) {
                        call.respond(HttpStatusCode.OK, MessageResponse("Artista eliminado"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("No encontrado"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                }
            }
        }
    }
}