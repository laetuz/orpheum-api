package id.neotica.domain.model.catalog

import kotlinx.serialization.Serializable

@Serializable
data class AlbumWithTracks(
    val album: Album,
    val tracks: List<Track>
)