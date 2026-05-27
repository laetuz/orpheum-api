package id.neotica.domain.model.catalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String,
    @SerialName("album_id") val albumId: String,
    val title: String,
    @SerialName("duration_seconds") val durationSeconds: Int,
    @SerialName("file_url") val fileUrl: String,
    @SerialName("track_number") val trackNumber: Int
)