package id.neotica.data.dao.playlist.playlist

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.datetime

object PlaylistTable : UUIDTable("playlists") {
    val userId = varchar("user_id", 128).index()
    val slug = varchar("slug", 255).uniqueIndex()
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val isPublic = bool("is_public").default(false)
    val createdAt = datetime("created_at")
}