package id.neotica.route

import id.neotica.domain.repository.CatalogRepository
import id.neotica.utils.getPaginationParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.catalogRoutes() {
    // Inject the interface. Koin provides CatalogRepositoryImpl.
    val repository by inject<CatalogRepository>()

    route("/catalog") {

        // GET /catalog/new-releases?page=1&limit=20
        get("/new-releases") {
            val pagination = call.getPaginationParams()
            val feed = repository.getNewReleases(pagination)

            call.respond(HttpStatusCode.OK, feed)
        }
    }

    route("/artists") {

        // GET /artists/{artist_id}/albums
        get("/{artist_id}/albums") {
            val artistId = call.parameters["artist_id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing artist_id")

            try {
                val albums = repository.getArtistAlbums(artistId)
                call.respond(HttpStatusCode.OK, albums)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid artist ID format")
            }
        }
    }

    route("/albums") {

        // GET /albums/{album_id}
        get("/{album_id}") {
            val albumId = call.parameters["album_id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing album_id")

            try {
                val albumData = repository.getAlbumWithTracks(albumId)
                if (albumData != null) {
                    call.respond(HttpStatusCode.OK, albumData)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Album not found")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid album ID format")
            }
        }
    }
}