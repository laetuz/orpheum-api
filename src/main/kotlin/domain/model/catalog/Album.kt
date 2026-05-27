package id.neotica.domain.model.catalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    @SerialName("artist_id") val artistId: String,
    val title: String,
    @SerialName("release_year") val releaseYear: Int,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("created_at") val createdAt: String
)