package id.neotica.domain.model.catalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val bio: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String
)