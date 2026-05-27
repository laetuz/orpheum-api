package id.neotica.domain.model.playlist

import id.neotica.domain.model.catalog.Track
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistWithTracks(
    val playlist: Playlist,
    val tracks: List<Track>
)
