package id.neotica.data.dao.playlist.playlisttrack

import id.neotica.data.dao.catalog.track.TrackTable
import id.neotica.data.dao.playlist.playlist.PlaylistTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.datetime

object PlaylistTrackTable : UUIDTable("playlist_tracks") {
    val playlistId = reference("playlist_id", PlaylistTable, onDelete = ReferenceOption.CASCADE)
    val trackId = reference("track_id", TrackTable, onDelete = ReferenceOption.CASCADE)
    val sortOrder = integer("sort_order")
    val addedAt = datetime("added_at")
}