package id.neotica.data.dao.catalog.artist

import id.neotica.data.dao.catalog.album.AlbumEntity
import id.neotica.data.dao.catalog.album.AlbumTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class ArtistEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ArtistEntity>(ArtistTable)

    var name by ArtistTable.name
    var bio by ArtistTable.bio
    var imageUrl by ArtistTable.imageUrl
    var createdAt by ArtistTable.createdAt

    val albums by AlbumEntity referrersOn AlbumTable.artistId
}