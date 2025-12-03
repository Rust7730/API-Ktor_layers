package com.example.routes

import com.example.model.TrackRequest
import com.example.model.TrackResponse
import com.example.model.ErrorResponse
import com.example.model.MessageResponse
import com.example.service.TrackService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.trackRoutes(trackService: TrackService) {
    route("/tracks") {

        //  Crear
        post {
            try {
                val request = call.receive<TrackRequest>()
                val albumId = UUID.fromString(request.albumId)
                val newId = trackService.create(request.title, request.duration, albumId)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error: ${e.message}"))
            }
        }

        //  Listar
        get {
            try {
                val tracks = trackService.getAll().map {
                    TrackResponse(it.id.toString(), it.title, it.duration, it.albumId.toString())
                }
                call.respond(HttpStatusCode.OK, tracks)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error: ${e.message}"))
            }
        }

        //  Por ID
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val track = trackService.getById(id)
                if (track != null) {
                    call.respond(HttpStatusCode.OK, TrackResponse(track.id.toString(), track.title, track.duration, track.albumId.toString()))
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
                val request = call.receive<TrackRequest>()
                val albumId = UUID.fromString(request.albumId)
                if (trackService.update(id, request.title, request.duration, albumId)) {
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
                if (trackService.delete(id)) {
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