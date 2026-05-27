package id.neotica.domain.repository.mapper

import id.neotica.data.dao.catalog.album.AlbumEntity
import id.neotica.data.dao.catalog.album.AlbumTable
import id.neotica.data.dao.catalog.artist.ArtistEntity
import id.neotica.data.dao.catalog.track.TrackEntity
import id.neotica.domain.model.catalog.Album
import id.neotica.domain.model.catalog.Artist
import id.neotica.domain.model.catalog.Track
import org.jetbrains.exposed.v1.core.ResultRow

fun ArtistEntity.toArtist() = Artist(
    id = this.id.value.toString(),
    name = this.name,
    bio = this.bio,
    imageUrl = this.imageUrl,
    createdAt = this.createdAt.toString()
)

fun AlbumEntity.toAlbum() = Album(
    id = this.id.value.toString(),
    // Pulled securely through the DAO relationship without manual joins
    artistId = this.artist.id.value.toString(),
    title = this.title,
    releaseYear = this.releaseYear,
    coverUrl = this.coverUrl,
    createdAt = this.createdAt.toString()
)

fun TrackEntity.toTrack() = Track(
    id = this.id.value.toString(),
    // Pulled securely through the DAO relationship without manual joins
    albumId = this.album.id.value.toString(),
    title = this.title,
    durationSeconds = this.durationSeconds,
    fileUrl = this.fileUrl,
    trackNumber = this.trackNumber
)

fun ResultRow.toAlbum() = Album(
    id = this[AlbumTable.id].value.toString(),
    artistId = this[AlbumTable.artistId].value.toString(),
    title = this[AlbumTable.title],
    releaseYear = this[AlbumTable.releaseYear],
    coverUrl = this[AlbumTable.coverUrl],
    createdAt = this[AlbumTable.createdAt].toString()
)