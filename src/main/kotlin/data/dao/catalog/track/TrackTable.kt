package id.neotica.data.dao.catalog.track

import id.neotica.data.dao.catalog.album.AlbumTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object TrackTable : UUIDTable("tracks") {
    val albumId = reference("album_id", AlbumTable, onDelete = ReferenceOption.CASCADE).index()
    val title = varchar("title", 255)
    val durationSeconds = integer("duration_seconds")
    val fileUrl = varchar("file_url", 512)
    val trackNumber = integer("track_number")
}