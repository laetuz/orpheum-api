package id.neotica.data.dao.catalog.album

import id.neotica.data.dao.catalog.artist.ArtistTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.datetime

object AlbumTable : UUIDTable("albums") {
    val artistId = reference("artist_id", ArtistTable, onDelete = ReferenceOption.CASCADE).index()
    val title = varchar("title", 255)
    val releaseYear = integer("release_year")
    val coverUrl = varchar("cover_url", 512).nullable()
    val createdAt = datetime("created_at")
}