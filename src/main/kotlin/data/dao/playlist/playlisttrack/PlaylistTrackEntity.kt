package id.neotica.data.dao.playlist.playlisttrack

import id.neotica.data.dao.catalog.track.TrackEntity
import id.neotica.data.dao.playlist.playlist.PlaylistEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class PlaylistTrackEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlaylistTrackEntity>(PlaylistTrackTable)

    var playlist by PlaylistEntity referencedOn PlaylistTrackTable.playlistId
    var track by TrackEntity referencedOn PlaylistTrackTable.trackId
    var sortOrder by PlaylistTrackTable.sortOrder
    var addedAt by PlaylistTrackTable.addedAt
}