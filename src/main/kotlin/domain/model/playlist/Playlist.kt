package id.neotica.domain.model.playlist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: String,
    @SerialName("user_id") val userId: String,
    val slug: String,
    val title: String,
    val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean,
    @SerialName("created_at") val createdAt: String
)