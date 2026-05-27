package id.neotica.data.dao.catalog.artist

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.datetime

object ArtistTable : UUIDTable("artists") {
    val name = varchar("name", 255)
    val bio = text("bio").nullable()
    val imageUrl = varchar("image_url", 512).nullable()
    val createdAt = datetime("created_at")
}