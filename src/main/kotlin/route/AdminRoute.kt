package id.neotica.route

import id.neotica.domain.model.catalog.request.CreateAlbumRequest
import id.neotica.domain.model.catalog.request.CreateArtistRequest
import id.neotica.domain.repository.CatalogRepository
import id.neotica.utils.Constants.AUTH_JWT
import id.neotica.utils.Constants.WEED_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.http.isSuccess
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

class AdminRoute(
    private val repository: CatalogRepository,
    private val httpClient: HttpClient
) {
    operator fun invoke(route: Route) = route.route("") {
        val seaweedBaseUrl = WEED_URL

        authenticate(AUTH_JWT) {
            route("/admin") {

                post("/artists") {
                    val request = call.receive<CreateArtistRequest>()
                    val artist = repository.createArtist(request.name, request.bio, request.imageUrl)
                    call.respond(HttpStatusCode.Created, artist)
                }

                post("/albums") {
                    val request = call.receive<CreateAlbumRequest>()
                    val album = repository.createAlbum(request.artistId, request.title, request.releaseYear, request.coverUrl)
                    if (album != null) {
                        call.respond(HttpStatusCode.Created, album)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid artist ID.")
                    }
                }

                post("/tracks") {
                    var albumId = ""
                    var title = ""
                    var trackNumber = 1
                    var durationSeconds = 0
                    var fileBytes: ByteArray? = null
                    var originalFileName = ""

                    val multipart = call.receiveMultipart()
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "album_id" -> albumId = part.value
                                    "title" -> title = part.value
                                    "track_number" -> trackNumber = part.value.toIntOrNull() ?: 1
                                    "duration_seconds" -> durationSeconds = part.value.toIntOrNull() ?: 0
                                }
                            }
                            is PartData.FileItem -> {
                                originalFileName = part.originalFileName ?: "unknown.mp3"
                                fileBytes = part.streamProvider().readBytes()
                            }
                            else -> Unit
                        }
                        part.dispose()
                    }

                    if (fileBytes == null || albumId.isEmpty() || title.isEmpty()) {
                        return@post call.respond(HttpStatusCode.BadRequest, "Missing required fields or audio file.")
                    }

                    val safeName = originalFileName.replace(Regex("[^a-zA-Z0-9.-]"), "_")
                    val seaweedPath = "orpheum/${UUID.randomUUID()}-$safeName"

                    try {
                        println("▶️ Attempting to upload file to: $seaweedBaseUrl/$seaweedPath")

                        // 🚨 Changed to PUT
                        val uploadResponse = httpClient.put("$seaweedBaseUrl/$seaweedPath") {
                            setBody(ByteArrayContent(fileBytes, ContentType.Audio.MPEG))
                        }

                        if (!uploadResponse.status.isSuccess()) {
                            val errorBody = uploadResponse.bodyAsText()
                            println("❌ Storage Server Rejected Upload. Status: ${uploadResponse.status} Body: $errorBody")
                            return@post call.respond(HttpStatusCode.InternalServerError, "Storage server rejected the file.")
                        }

                        println("✅ File successfully uploaded to storage vault!")

                    } catch (e: Exception) {
                        println("💥 HTTP Client Crash: Failed to connect to storage.")
                        e.printStackTrace()
                        return@post call.respond(HttpStatusCode.InternalServerError, "Failed to connect to storage vault: ${e.message}")
                    }

                    val track = repository.createTrack(albumId, title, trackNumber, durationSeconds, seaweedPath)

                    if (track != null) {
                        call.respond(HttpStatusCode.Created, track)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Failed to map track to database. Verify Album ID.")
                    }
                }
            }
        }
    }
}