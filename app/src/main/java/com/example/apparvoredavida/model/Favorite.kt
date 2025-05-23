package com.example.apparvoredavida.model

sealed class Favorite {
    data class Music(
        val id: String,
        val title: String,
        val artist: String?,
        val path: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite()

    data class Verse(
        val id: String,
        val book: String,
        val chapter: Int,
        val verse: Int,
        val text: String,
        val translation: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite()

    data class Hymn(
        val id: String,
        val number: Int,
        val title: String,
        val author: String?,
        val lyrics: String?,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite()

    data class Score(
        val id: String,
        val title: String,
        val path: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : Favorite()
} 