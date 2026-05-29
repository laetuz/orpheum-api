package id.neotica.domain.model.catalog.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAlbumRequest(
    @SerialName("artist_id") val artistId: String,
    val title: String,
    @SerialName("release_year") val releaseYear: Int,
    @SerialName("cover_url") val coverUrl: String? = null
)