package id.neotica.data.repository

import id.neotica.data.dao.catalog.album.AlbumEntity
import id.neotica.data.dao.catalog.album.AlbumTable
import id.neotica.data.dao.catalog.track.TrackEntity
import id.neotica.data.dao.catalog.track.TrackTable
import id.neotica.domain.NeoDatabase
import id.neotica.domain.model.catalog.Album
import id.neotica.domain.model.catalog.AlbumWithTracks
import id.neotica.domain.model.catalog.Track
import id.neotica.domain.repository.CatalogRepository
import id.neotica.domain.repository.mapper.toAlbum
import id.neotica.domain.repository.mapper.toTrack
import id.neotica.utils.PaginatedResponse
import id.neotica.utils.PaginationParams
import id.neotica.utils.paginate
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import java.util.UUID

class CatalogRepositoryImpl(
    private val db: NeoDatabase
) : CatalogRepository {

    override suspend fun getNewReleases(params: PaginationParams): PaginatedResponse<Album> = db.dbQuery {
        AlbumEntity
            .all()
            .orderBy(AlbumTable.createdAt to SortOrder.DESC)
            .paginate(params) { it.toAlbum() }
    }

    override suspend fun getArtistAlbums(artistId: String): List<Album> = db.dbQuery {
        val uuid = UUID.fromString(artistId)

        AlbumEntity
            .find { AlbumTable.artistId.eq(uuid) }
            .orderBy(AlbumTable.releaseYear to SortOrder.DESC)
            .map { it.toAlbum() }
    }

    override suspend fun getAlbumWithTracks(albumId: String): AlbumWithTracks? = db.dbQuery {
        val uuid = UUID.fromString(albumId)

        // 1. Fetch the Album Entity
        val albumEntity = AlbumEntity.findById(uuid) ?: return@dbQuery null

        // 2. Fetch tracks using the DAO relationship!
        // No need to write a manual TrackTable query, Exposed handles the join.
        val sortedTracks = albumEntity.tracks
            .orderBy(TrackTable.trackNumber to SortOrder.ASC)
            .map { it.toTrack() }

        // 3. Bundle them together
        AlbumWithTracks(
            album = albumEntity.toAlbum(),
            tracks = sortedTracks
        )
    }

    override suspend fun getTrackDetails(trackId: String): Track? = db.dbQuery {
        val uuid = UUID.fromString(trackId)
        TrackEntity.findById(uuid)?.toTrack()
    }
}