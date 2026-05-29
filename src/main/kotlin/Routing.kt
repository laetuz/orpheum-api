package id.neotica

import id.neotica.route.AdminRoute
import id.neotica.route.CatalogRoute
import id.neotica.route.PlaylistRoute
import id.neotica.route.StreamRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val catalogRoute by inject<CatalogRoute>()
    val adminRoute by inject<AdminRoute>()
    val playlistRoute by inject<PlaylistRoute>()
    val streamRoute by inject<StreamRoute>()

    routing {
        get("/") {
            call.respondText("Orpheum API v0.1.0 is online and ready to stream.")
        }

        catalogRoute.invoke(this)
        adminRoute.invoke(this)
        playlistRoute.invoke(this)
        streamRoute.invoke(this)
    }
}