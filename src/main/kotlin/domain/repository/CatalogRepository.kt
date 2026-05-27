package id.neotica.domain.repository

import id.neotica.domain.model.catalog.Album
import id.neotica.domain.model.catalog.AlbumWithTracks
import id.neotica.domain.model.catalog.Track
import id.neotica.utils.PaginatedResponse
import id.neotica.utils.PaginationParams

interface CatalogRepository {
    suspend fun getNewReleases(params: PaginationParams): PaginatedResponse<Album>
    suspend fun getArtistAlbums(artistId: String): List<Album>
    suspend fun getAlbumWithTracks(albumId: String): AlbumWithTracks?
    suspend fun getTrackDetails(trackId: String): Track?
}