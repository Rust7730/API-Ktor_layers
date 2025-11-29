package com.example.routes

import com.example.model.ArtistRequest
import com.example.model.ArtistResponse
import com.example.model.ErrorResponse
import com.example.model.MessageResponse
import com.example.service.ArtistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.artistRoutes() {
    val artistService = ArtistService()

    route("/artists") {

        // GET /artists - Obtener todos los artistas (público)
        get {
            try {
                val artists = artistService.getAll()
                val response = artists.map { artist ->
                    ArtistResponse(
                        id = artist.id.toString(),
                        name = artist.name,
                        genre = artist.genre,
                        image = artist.image
                    )
                }
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("Error al obtener artistas: ${e.message}")
                )
            }
        }

        // Obtener un artista por ID (público)
        get("/{id}") {
            try {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID no proporcionado"))
                    return@get
                }

                val uuid = try {
                    UUID.fromString(id)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                    return@get
                }

                val artist = artistService.getById(uuid)
                if (artist != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        ArtistResponse(
                            id = artist.id.toString(),
                            name = artist.name,
                            genre = artist.genre,
                            image = artist.image
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Artista no encontrado"))
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("Error al obtener artista: ${e.message}")
                )
            }
        }

        //  Crear artista (solo ADMIN)
        authenticate("auth-jwt") {
            post {
                try {
                    // Verificar rol de ADMIN
                    val principal = call.principal<JWTPrincipal>()
                    val role = principal?.payload?.getClaim("role")?.asString()

                    if (role != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            ErrorResponse("No tienes permisos para realizar esta acción")
                        )
                        return@post
                    }

                    val request = call.receive<ArtistRequest>()

                    if (request.name.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("El nombre es obligatorio"))
                        return@post
                    }

                    val artistId = artistService.create(request.name, request.genre, request.image)

                    call.respond(
                        HttpStatusCode.Created,
                        ArtistResponse(
                            id = artistId.toString(),
                            name = request.name,
                            genre = request.genre,
                            image = request.image
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Error al crear artista: ${e.message}")
                    )
                }
            }

            //  Actualizar artista (solo ADMIN)
            put("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val role = principal?.payload?.getClaim("role")?.asString()

                    if (role != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            ErrorResponse("No tienes permisos para realizar esta acción")
                        )
                        return@put
                    }

                    val id = call.parameters["id"]
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID no proporcionado"))
                        return@put
                    }

                    val uuid = try {
                        UUID.fromString(id)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                        return@put
                    }

                    val request = call.receive<ArtistRequest>()

                    if (request.name.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("El nombre es obligatorio"))
                        return@put
                    }

                    val updated = artistService.update(uuid, request.name, request.genre, request.image)

                    if (updated) {
                        call.respond(
                            HttpStatusCode.OK,
                            MessageResponse("Artista actualizado exitosamente")
                        )
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("Artista no encontrado"))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Error al actualizar artista: ${e.message}")
                    )
                }
            }

            // DELETE /artists/{id} - Eliminar artista (solo ADMIN)
            delete("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val role = principal?.payload?.getClaim("role")?.asString()

                    if (role != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            ErrorResponse("No tienes permisos para realizar esta acción")
                        )
                        return@delete
                    }

                    val id = call.parameters["id"]
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID no proporcionado"))
                        return@delete
                    }

                    val uuid = try {
                        UUID.fromString(id)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                        return@delete
                    }

                    val deleted = artistService.delete(uuid)

                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            MessageResponse("Artista eliminado exitosamente")
                        )
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("Artista no encontrado"))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Error al eliminar artista: ${e.message}")
                    )
                }
            }
        }
    }
}