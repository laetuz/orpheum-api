package id.neotica.data.repository

import id.neotica.data.dao.catalog.track.TrackEntity
import id.neotica.data.dao.playlist.playlist.PlaylistEntity
import id.neotica.data.dao.playlist.playlist.PlaylistTable
import id.neotica.data.dao.playlist.playlisttrack.PlaylistTrackEntity
import id.neotica.data.dao.playlist.playlisttrack.PlaylistTrackTable
import id.neotica.domain.NeoDatabase
import id.neotica.domain.model.catalog.Track
import id.neotica.domain.model.playlist.Playlist
import id.neotica.domain.repository.PlaylistRepository
import id.neotica.domain.repository.mapper.toPlaylist
import id.neotica.domain.repository.mapper.toTrack
import id.neotica.utils.PaginatedResponse
import id.neotica.utils.PaginationParams
import id.neotica.utils.paginate
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import java.time.LocalDateTime
import java.util.UUID

class PlaylistRepositoryImpl(
    private val db: NeoDatabase
) : PlaylistRepository {

    override suspend fun getUserPlaylists(userId: String, params: PaginationParams): PaginatedResponse<Playlist> = db.dbQuery {
        PlaylistEntity
            .find { PlaylistTable.userId.eq(userId) }
            .orderBy(PlaylistTable.createdAt to SortOrder.DESC)
            .paginate(params) { it.toPlaylist() }
    }

    override suspend fun getPlaylistDetails(slug: String): Playlist? = db.dbQuery {
        PlaylistEntity
            .find { PlaylistTable.slug eq slug }
            .singleOrNull()
            ?.toPlaylist()
    }

    override suspend fun getPlaylistTracks(playlistId: String): List<Track> = db.dbQuery {
        val pId = UUID.fromString(playlistId)

        PlaylistTrackEntity
            .find { PlaylistTrackTable.playlistId eq pId }
            .orderBy(PlaylistTrackTable.sortOrder to SortOrder.ASC)
            .map { it.track.toTrack() }
    }

    override suspend fun createPlaylist(
        userId: String,
        title: String,
        description: String?,
        isPublic: Boolean
    ): Playlist = db.dbQuery {
        val generatedSlug = title.lowercase().replace(Regex("[^a-z0-9]+"), "-") + "-" + UUID.randomUUID().toString().take(8)

        PlaylistEntity.new {
            this.userId = userId
            this.slug = generatedSlug
            this.title = title
            this.description = description
            this.isPublic = isPublic
            this.createdAt = LocalDateTime.now()
        }.toPlaylist()
    }

    override suspend fun addTrackToPlaylist(playlistId: String, trackId: String, userId: String): Boolean = db.dbQuery {
        val pId = UUID.fromString(playlistId)
        val tId = UUID.fromString(trackId)

        val playlist = PlaylistEntity.find {
            (PlaylistTable.id eq pId).and(PlaylistTable.userId eq userId)
        }.singleOrNull() ?: return@dbQuery false

        val track = TrackEntity.findById(tId) ?: return@dbQuery false

        val currentMaxSort = PlaylistTrackEntity
            .find { PlaylistTrackTable.playlistId eq pId }
            .maxOfOrNull { it.sortOrder } ?: 0

        PlaylistTrackEntity.new {
            this.playlist = playlist
            this.track = track
            this.sortOrder = currentMaxSort + 1
            this.addedAt = LocalDateTime.now()
        }
        true
    }

    override suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String, userId: String): Boolean = db.dbQuery {
        val pId = UUID.fromString(playlistId)
        val tId = UUID.fromString(trackId)

        PlaylistEntity.find {
            (PlaylistTable.id eq pId) and (PlaylistTable.userId eq userId)
        }.singleOrNull() ?: return@dbQuery false

        val link = PlaylistTrackEntity.find {
            (PlaylistTrackTable.playlistId eq pId) and (PlaylistTrackTable.trackId eq tId)
        }.singleOrNull()

        if (link != null) {
            link.delete()
            true
        } else {
            false
        }
    }
}