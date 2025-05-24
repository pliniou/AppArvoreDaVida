package com.example.apparvoredavida.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FavoritesRepository {
    val favoriteMusicIdsFlow: StateFlow<Set<String>>
    val favoriteVerseIdsFlow: StateFlow<Set<String>>
    val favoriteHymnIdsFlow: StateFlow<Set<String>>
    val favoriteScoreIdsFlow: StateFlow<Set<String>>

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