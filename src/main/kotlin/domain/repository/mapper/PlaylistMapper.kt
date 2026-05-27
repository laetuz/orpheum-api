package id.neotica.domain.repository.mapper

import id.neotica.data.dao.playlist.playlist.PlaylistEntity
import id.neotica.domain.model.playlist.Playlist

fun PlaylistEntity.toPlaylist() = Playlist(
    id = this.id.value.toString(),
    userId = this.userId,
    slug = this.slug,
    title = this.title,
    description = this.description,
    isPublic = this.isPublic,
    createdAt = this.createdAt.toString()
)