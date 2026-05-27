package id.neotica.domain.repository

import id.neotica.domain.model.catalog.Track
import id.neotica.domain.model.playlist.Playlist
import id.neotica.utils.PaginatedResponse
import id.neotica.utils.PaginationParams

interface PlaylistRepository {
    suspend fun getUserPlaylists(userId: String, params: PaginationParams): PaginatedResponse<Playlist>

    suspend fun getPlaylistDetails(slug: String): Playlist?
    suspend fun getPlaylistTracks(playlistId: String): List<Track>

    suspend fun createPlaylist(userId: String, title: String, description: String?, isPublic: Boolean): Playlist
    suspend fun addTrackToPlaylist(playlistId: String, trackId: String, userId: String): Boolean
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String, userId: String): Boolean
}