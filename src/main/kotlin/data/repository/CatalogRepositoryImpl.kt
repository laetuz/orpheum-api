package id.neotica.data.repository

import id.neotica.data.dao.catalog.album.AlbumEntity
import id.neotica.data.dao.catalog.album.AlbumTable
import id.neotica.data.dao.catalog.artist.ArtistEntity
import id.neotica.data.dao.catalog.artist.ArtistTable
import id.neotica.data.dao.catalog.track.TrackEntity
import id.neotica.data.dao.catalog.track.TrackTable
import id.neotica.domain.NeoDatabase
import id.neotica.domain.model.catalog.Album
import id.neotica.domain.model.catalog.AlbumWithTracks
import id.neotica.domain.model.catalog.Artist
import id.neotica.domain.model.catalog.Track
import id.neotica.domain.repository.CatalogRepository
import id.neotica.domain.repository.mapper.toAlbum
import id.neotica.domain.repository.mapper.toArtist
import id.neotica.domain.repository.mapper.toTrack
import id.neotica.utils.PaginatedResponse
import id.neotica.utils.PaginationParams
import id.neotica.utils.paginate
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID

class CatalogRepositoryImpl(
    private val db: NeoDatabase
) : CatalogRepository {

    // 1. The renamed Album feed
    override suspend fun getNewAlbums(params: PaginationParams): PaginatedResponse<Album> = db.dbQuery {
        AlbumEntity.all()
            .orderBy(AlbumTable.createdAt to SortOrder.DESC)
            .paginate(params) { it.toAlbum() }
    }

    // 2. The brand-new Track feed
    override suspend fun getNewTracks(params: PaginationParams): PaginatedResponse<Track> = db.dbQuery {
        // We join the Track and Album tables so we can sort the tracks
        // by how recently their parent album was created.
        val query = TrackTable.innerJoin(AlbumTable)
            .selectAll()
            .orderBy(AlbumTable.createdAt to SortOrder.DESC)

        // wrapRows converts the raw SQL query back into your TrackEntity format
        // so it works perfectly with your existing pagination extension!
        TrackEntity.wrapRows(query).paginate(params) { it.toTrack() }
    }

    override suspend fun searchTracks(query: String, params: PaginationParams): PaginatedResponse<Track> = db.dbQuery {
        val q = query.lowercase()
        val searchQuery = TrackTable
            .innerJoin(AlbumTable)
            .innerJoin(ArtistTable)
            .selectAll()
            .where {
                (TrackTable.title.lowerCase() like "%$q%") or
                (ArtistTable.name.lowerCase() like "%$q%")
            }
            .orderBy(TrackTable.title to SortOrder.ASC)
        TrackEntity.wrapRows(searchQuery).paginate(params) { it.toTrack() }
    }

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

    override suspend fun createArtist(name: String, bio: String?, imageUrl: String?): Artist = db.dbQuery {
        val existingArtist = ArtistEntity.find {
            ArtistTable.name.lowerCase() eq name.lowercase()
        }.singleOrNull()

        if (existingArtist != null) {
            return@dbQuery existingArtist.toArtist()
        }

        ArtistEntity.new {
            this.name = name
            this.bio = bio
            this.imageUrl = imageUrl
            this.createdAt = java.time.LocalDateTime.now()
        }.toArtist()
    }

    override suspend fun createAlbum(artistId: String, title: String, releaseYear: Int, coverUrl: String?): Album? = db.dbQuery {
        val aId = try { UUID.fromString(artistId) } catch (e: Exception) { return@dbQuery null }
        val artistEntity = ArtistEntity.findById(aId) ?: return@dbQuery null

        val existingAlbum = AlbumEntity.find {
            (AlbumTable.artistId.eq(aId)) and (AlbumTable.title.lowerCase() eq title.lowercase())
        }.singleOrNull()

        if (existingAlbum != null) {
            return@dbQuery existingAlbum.toAlbum()
        }

        AlbumEntity.new {
            this.artist = artistEntity
            this.title = title
            this.releaseYear = releaseYear
            this.coverUrl = coverUrl
            this.createdAt = java.time.LocalDateTime.now()
        }.toAlbum()
    }

    override suspend fun createTrack(
        albumId: String, title: String, trackNumber: Int, durationSeconds: Int, fileUrl: String
    ): Track? = db.dbQuery {
        val aId = try { UUID.fromString(albumId) } catch (e: Exception) { return@dbQuery null }
        val albumEntity = AlbumEntity.findById(aId) ?: return@dbQuery null

        val existingTrack = TrackEntity.find {
            (TrackTable.albumId eq aId) and (TrackTable.title.lowerCase() eq title.lowercase())
        }.singleOrNull()

        if (existingTrack != null) {
            return@dbQuery null // 🚨 Rejected!
        }

        TrackEntity.new {
            this.album = albumEntity
            this.title = title
            this.trackNumber = trackNumber
            this.durationSeconds = durationSeconds
            this.fileUrl = fileUrl
        }.toTrack()
    }

    override suspend fun updateAlbum(
        albumId: String, title: String?, releaseYear: Int?, coverUrl: String?
    ): Album? = db.dbQuery {
        val uuid = try { UUID.fromString(albumId) } catch (e: Exception) { return@dbQuery null }
        val album = AlbumEntity.findById(uuid) ?: return@dbQuery null

        // Only update fields if they were provided in the request
        title?.let { album.title = it }
        releaseYear?.let { album.releaseYear = it }
        coverUrl?.let { album.coverUrl = it }

        album.toAlbum()
    }

    override suspend fun deleteAlbum(albumId: String): Boolean = db.dbQuery {
        val uuid = try { UUID.fromString(albumId) } catch (e: Exception) { return@dbQuery false }
        val album = AlbumEntity.findById(uuid) ?: return@dbQuery false

        album.delete()
        true
    }

    override suspend fun deleteTrack(trackId: String): Boolean = db.dbQuery {
        val uuid = try { UUID.fromString(trackId) } catch (e: Exception) { return@dbQuery false }
        val track = TrackEntity.findById(uuid) ?: return@dbQuery false

        track.delete()
        true
    }
}