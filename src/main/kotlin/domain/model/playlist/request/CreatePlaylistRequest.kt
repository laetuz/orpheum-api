package id.neotica.domain.model.playlist.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaylistRequest(
    val title: String,
    val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean = false
)