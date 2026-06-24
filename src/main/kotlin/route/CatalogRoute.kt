package id.neotica.route

import id.neotica.domain.repository.CatalogRepository
import id.neotica.utils.getPaginationParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

class CatalogRoute(private val repository: CatalogRepository) {
    operator fun invoke(route: Route) = route.route("") {
        route("/catalog") {
            get("/albums/new") {
                val pagination = call.getPaginationParams()
                val feed = repository.getNewAlbums(pagination)
                call.respond(HttpStatusCode.OK, feed)
            }

            // 🚨 Added tracks/new
            get("/tracks/new") {
                val pagination = call.getPaginationParams()
                val feed = repository.getNewTracks(pagination)
                call.respond(HttpStatusCode.OK, feed)
            }

            get("/new-releases") {
                val pagination = call.getPaginationParams()
                val feed = repository.getNewReleases(pagination)
                call.respond(HttpStatusCode.OK, feed)
            }

            get("/search") {
                val query = call.request.queryParameters["q"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing query parameter 'q'.")

                val pagination = call.getPaginationParams()
                val results = repository.searchTracks(query, pagination)
                call.respond(HttpStatusCode.OK, results)
            }
        }

        route("/artists") {
            get("/{artist_id}/albums") {
                val artistId = call.parameters["artist_id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing artist_id.")

                try {
                    val albums = repository.getArtistAlbums(artistId)
                    call.respond(HttpStatusCode.OK, albums)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid artist ID format.")
                }
            }
        }

        route("/albums") {
            get("/{album_id}") {
                val albumId = call.parameters["album_id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing album_id.")

                try {
                    val albumData = repository.getAlbumWithTracks(albumId)
                    if (albumData != null) {
                        call.respond(HttpStatusCode.OK, albumData)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Album not found.")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid album ID format.")
                }
            }
        }
    }
}