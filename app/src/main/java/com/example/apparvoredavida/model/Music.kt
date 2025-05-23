package com.example.apparvoredavida.model

import android.net.Uri
import androidx.media3.common.MediaItem

data class Music(
    val id: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val path: String,
    val coverPath: String?,
    val isFavorite: Boolean = false
) {
    fun toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(Uri.parse(path))
            .setMediaId(id)
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .build()
            )
            .build()
    }
} 