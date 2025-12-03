package com.example.routes

import com.example.model.ErrorResponse
import com.example.model.TrackRequest
import com.example.service.TrackService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.trackRoutes(trackService: TrackService) {
    route("/tracks") {

        // POST: Crear Track
        post {
            try {
                val request = call.receive<TrackRequest>()

                val albumUuid = UUID.fromString(request.albumId)

                val newId = trackService.create(request.title, request.duration, albumUuid)

                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Error al crear track: ${e.message}"))
            }
        }
    }
}