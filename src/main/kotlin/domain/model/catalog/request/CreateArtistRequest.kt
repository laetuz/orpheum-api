package id.neotica.domain.model.catalog.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateArtistRequest(
    val name: String,
    val bio: String? = null,
    @SerialName("image_url") val imageUrl: String? = null
)