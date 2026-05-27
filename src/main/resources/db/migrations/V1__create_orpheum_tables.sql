-- V1__create_orpheum_tables.sql

CREATE TABLE artists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    bio TEXT,
    image_url VARCHAR(512),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE albums (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    release_year INT NOT NULL,
    cover_url VARCHAR(512),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_albums_artist_id ON albums(artist_id);

CREATE TABLE tracks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    album_id UUID NOT NULL REFERENCES albums(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    duration_seconds INT NOT NULL,
    file_url VARCHAR(512) NOT NULL,
    track_number INT NOT NULL
);

CREATE INDEX idx_tracks_album_id ON tracks(album_id);

CREATE TABLE playlists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(128) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_playlists_user_id ON playlists(user_id);

CREATE TABLE playlist_tracks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    playlist_id UUID NOT NULL REFERENCES playlists(id) ON DELETE CASCADE,
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    sort_order INT NOT NULL,
    added_at TIMESTAMP NOT NULL
);