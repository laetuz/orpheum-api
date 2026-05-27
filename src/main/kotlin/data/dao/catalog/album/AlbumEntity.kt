package id.neotica.data.dao.catalog.album

import id.neotica.data.dao.catalog.artist.ArtistEntity
import id.neotica.data.dao.catalog.track.TrackEntity
import id.neotica.data.dao.catalog.track.TrackTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class AlbumEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AlbumEntity>(AlbumTable)

    var artist by ArtistEntity referencedOn AlbumTable.artistId
    var title by AlbumTable.title
    var releaseYear by AlbumTable.releaseYear
    var coverUrl by AlbumTable.coverUrl
    var createdAt by AlbumTable.createdAt

    val tracks by TrackEntity referrersOn TrackTable.albumId
}