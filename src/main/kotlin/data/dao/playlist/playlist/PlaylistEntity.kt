package id.neotica.data.dao.playlist.playlist

import id.neotica.data.dao.playlist.playlisttrack.PlaylistTrackEntity
import id.neotica.data.dao.playlist.playlisttrack.PlaylistTrackTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class PlaylistEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlaylistEntity>(PlaylistTable)

    var userId by PlaylistTable.userId
    var slug by PlaylistTable.slug
    var title by PlaylistTable.title
    var description by PlaylistTable.description
    var isPublic by PlaylistTable.isPublic
    var createdAt by PlaylistTable.createdAt

    val playlistTracks by PlaylistTrackEntity referrersOn PlaylistTrackTable.playlistId
}