package id.neotica

import id.neotica.route.catalogRoutes
import id.neotica.route.playlistRoutes
import id.neotica.route.streamRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Orpheum API v0.1.0 is online and ready to stream.")
        }
        catalogRoutes()
        playlistRoutes()
        streamRoutes()
    }
}