package id.neotica.route

import id.neotica.domain.model.playlist.PlaylistWithTracks
import id.neotica.domain.model.playlist.request.CreatePlaylistRequest
import id.neotica.domain.model.playlist.request.PlaylistTrackRequest
import id.neotica.domain.repository.PlaylistRepository
import id.neotica.utils.Constants.AUTH_JWT
import id.neotica.utils.getPaginationParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

class PlaylistRoute(private val repository: PlaylistRepository) {
    operator fun invoke(route: Route) = route.route("") {

        authenticate(AUTH_JWT, optional = true) {
            get("/playlists/{slug}") {
                val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing playlist slug.")
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString()

                val playlist = repository.getPlaylistDetails(slug) ?: return@get call.respond(HttpStatusCode.NotFound, "Playlist not found.")

                if (!playlist.isPublic && playlist.userId != userId) {
                    return@get call.respond(HttpStatusCode.Forbidden, "This playlist is private.")
                }

                val tracks = repository.getPlaylistTracks(playlist.id)
                call.respond(HttpStatusCode.OK, PlaylistWithTracks(playlist, tracks))
            }
        }

        authenticate(AUTH_JWT) {
            route("/playlists") {
                get {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asString()
                    val pagination = call.getPaginationParams()

                    val playlists = repository.getUserPlaylists(userId, pagination)
                    call.respond(HttpStatusCode.OK, playlists)
                }

                post {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asString()
                    val request = call.receive<CreatePlaylistRequest>()

                    val newPlaylist = repository.createPlaylist(
                        userId = userId,
                        title = request.title,
                        description = request.description,
                        isPublic = request.isPublic
                    )
                    call.respond(HttpStatusCode.Created, newPlaylist)
                }

                post("/{slug}/tracks") {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asString()
                    val slug = call.parameters["slug"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing playlist slug.")
                    val request = call.receive<PlaylistTrackRequest>()

                    val playlist = repository.getPlaylistDetails(slug) ?: return@post call.respond(HttpStatusCode.NotFound, "Playlist not found.")

                    val success = repository.addTrackToPlaylist(playlist.id, request.trackId, userId)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Track added successfully.")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Failed to add track. Verify ownership and track ID.")
                    }
                }

                delete("/{slug}/tracks/{track_id}") {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asString()
                    val slug = call.parameters["slug"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing playlist slug.")
                    val trackId = call.parameters["track_id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing track ID.")

                    val playlist = repository.getPlaylistDetails(slug) ?: return@delete call.respond(HttpStatusCode.NotFound, "Playlist not found.")

                    val success = repository.removeTrackFromPlaylist(playlist.id, trackId, userId)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Track removed successfully.")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Failed to remove track.")
                    }
                }
            }
        }
    }
}