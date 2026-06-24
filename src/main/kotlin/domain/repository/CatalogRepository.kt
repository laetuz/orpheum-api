package id.neotica.domain.repository

import id.neotica.domain.model.catalog.Album
import id.neotica.domain.model.catalog.AlbumWithTracks
import id.neotica.domain.model.catalog.Artist
import id.neotica.domain.model.catalog.Track
import id.neotica.utils.PaginatedResponse
import id.neotica.utils.PaginationParams

interface CatalogRepository {
    suspend fun getNewAlbums(params: PaginationParams): PaginatedResponse<Album>
    suspend fun getNewTracks(params: PaginationParams): PaginatedResponse<Track>
    suspend fun getNewReleases(params: PaginationParams): PaginatedResponse<Album>
    suspend fun getArtistAlbums(artistId: String): List<Album>
    suspend fun getAlbumWithTracks(albumId: String): AlbumWithTracks?
    suspend fun getTrackDetails(trackId: String): Track?
    suspend fun searchTracks(query: String, params: PaginationParams): PaginatedResponse<Track>
    suspend fun createArtist(name: String, bio: String?, imageUrl: String?): Artist
    suspend fun createAlbum(artistId: String, title: String, releaseYear: Int, coverUrl: String?): Album?
    suspend fun createTrack(albumId: String, title: String, trackNumber: Int, durationSeconds: Int, fileUrl: String): Track?
    suspend fun updateAlbum(albumId: String, title: String?, releaseYear: Int?, coverUrl: String?): Album?
    suspend fun deleteAlbum(albumId: String): Boolean
    suspend fun deleteTrack(trackId: String): Boolean
}