package com.example.apparvoredavida.data.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favoriteMusicIdsFlow: Flow<Set<String>>
    val favoriteVerseIdsFlow: Flow<Set<String>>
    val favoriteHymnIdsFlow: Flow<Set<String>>
    val favoriteScoreIdsFlow: Flow<Set<String>>

    suspend fun addFavoriteMusic(musicId: String)
    suspend fun removeFavoriteMusic(musicId: String)
    suspend fun addFavoriteVerse(verseId: String)
    suspend fun removeFavoriteVerse(verseId: String)
    suspend fun addFavoriteHymn(hymnId: String)
    suspend fun removeFavoriteHymn(hymnId: String)
    suspend fun addFavoriteScore(scoreId: String)
    suspend fun removeFavoriteScore(scoreId: String)

    fun isMusicFavoriteFlow(musicId: String): Flow<Boolean>
    fun isVerseFavoriteFlow(verseId: String): Flow<Boolean>
    fun isHymnFavoriteFlow(hymnId: String): Flow<Boolean>
    fun isScoreFavoriteFlow(scoreId: String): Flow<Boolean>
} 