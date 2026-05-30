package id.neotica.domain.model.catalog.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditAlbumRequest(
    val title: String? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    @SerialName("cover_url") val coverUrl: String? = null
)