package id.neotica.route

import id.neotica.domain.repository.CatalogRepository
import id.neotica.utils.Constants.AUTH_JWT
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyAndClose
import org.koin.ktor.ext.inject

fun Route.streamRoutes() {
    val repository by inject<CatalogRepository>()
    val httpClient by inject<HttpClient>()

    // The air-gapped SeaweedFS URL (Loaded from environment in production)
    val seaweedBaseUrl = "http://localhost:8090"

    authenticate(AUTH_JWT) {
        get("/stream/{track_id}") {
            val trackId = call.parameters["track_id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing track_id.")

            // 1. Ask the database where the file lives
            val track = repository.getTrackDetails(trackId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Track not found in database.")

            // 2. Knock on SeaweedFS's door
            val seaweedResponse = httpClient.get("$seaweedBaseUrl/${track.fileUrl}")

            if (!seaweedResponse.status.isSuccess()) {
                return@get call.respond(HttpStatusCode.BadGateway, "Failed to fetch media from storage.")
            }

            // 3. Create the Transparent Pipe
            call.respond(object : OutgoingContent.WriteChannelContent() {
                override val contentLength = seaweedResponse.contentLength()
                override val contentType = ContentType.parse(seaweedResponse.contentType()?.toString() ?: "audio/mpeg")

                override suspend fun writeTo(channel: ByteWriteChannel) {
                    // This streams the data chunk-by-chunk directly to the user
                    seaweedResponse.bodyAsChannel().copyAndClose(channel)
                }
            })
        }
    }
}