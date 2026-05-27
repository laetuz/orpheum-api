package id.neotica.domain.model.playlist.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistTrackRequest(
    @SerialName("track_id") val trackId: String
)