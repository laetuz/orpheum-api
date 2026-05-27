package id.neotica.data.dao.catalog.track

import id.neotica.data.dao.catalog.album.AlbumEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class TrackEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TrackEntity>(TrackTable)

    var album by AlbumEntity referencedOn TrackTable.albumId
    var title by TrackTable.title
    var durationSeconds by TrackTable.durationSeconds
    var fileUrl by TrackTable.fileUrl
    var trackNumber by TrackTable.trackNumber
}