package id.neotica.route

import id.neotica.domain.repository.CatalogRepository
import id.neotica.utils.Constants.WEED_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyAndClose

class StreamRoute(
    private val repository: CatalogRepository,
    private val httpClient: HttpClient
) {
    operator fun invoke(route: Route) = route.route("") {
        val seaweedBaseUrl = WEED_URL

        get("/stream/{track_id}") {
            val trackId = call.parameters["track_id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing track_id.")

            val track = repository.getTrackDetails(trackId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Track not found in database.")

            val seaweedResponse = httpClient.get("$seaweedBaseUrl/${track.fileUrl}")

            if (!seaweedResponse.status.isSuccess()) {
                return@get call.respond(HttpStatusCode.BadGateway, "Failed to fetch media from storage.")
            }

            call.respond(object : OutgoingContent.WriteChannelContent() {
                override val contentLength = seaweedResponse.contentLength()
                override val contentType = ContentType.parse(seaweedResponse.contentType()?.toString() ?: "audio/mpeg")

                override suspend fun writeTo(channel: ByteWriteChannel) {
                    seaweedResponse.bodyAsChannel().copyAndClose(channel)
                }
            })
        }
    }
}