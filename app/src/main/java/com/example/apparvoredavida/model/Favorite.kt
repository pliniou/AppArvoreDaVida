package com.example.apparvoredavida.model

sealed class Favorite(open val id: String, open val title: String) {
    data class Music(
        override val id: String,
        override val title: String,
        val artist: String?,
        val path: String,
        val album: String? = null,
        val coverPath: String? = null,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite(id, title)

    data class Verse(
        override val id: String,
        override val title: String,
        val book: String,
        val chapter: Int,
        val verse: Int,
        val text: String,
        val translation: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite(id, title)

    data class Hymn(
        override val id: String,
        override val title: String,
        val number: Int,
        val author: String?,
        val lyrics: String?,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite(id, title)

    data class Score(
        override val id: String,
        override val title: String,
        val path: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite(id, title)
} 