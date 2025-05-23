package com.example.apparvoredavida.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // --- Música ---
    val favoriteMusicIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet()
        }.distinctUntilChanged()

    suspend fun addFavoriteMusic(musicId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] = currentFavorites + musicId
        }
    }

    suspend fun removeFavoriteMusic(musicId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] = currentFavorites - musicId
        }
    }

    fun isMusicFavoriteFlow(musicId: String): Flow<Boolean> {
        return favoriteMusicIdsFlow.map { it.contains(musicId) }.distinctUntilChanged()
    }

    // --- Versículo ---
    val favoriteVerseIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet()
        }.distinctUntilChanged()

    suspend fun addFavoriteVerse(verseId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_VERSE_IDS] = currentFavorites + verseId
        }
    }

    suspend fun removeFavoriteVerse(verseId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_VERSE_IDS] = currentFavorites - verseId
        }
    }

    fun isVerseFavoriteFlow(verseId: String): Flow<Boolean> {
        return favoriteVerseIdsFlow.map { it.contains(verseId) }.distinctUntilChanged()
    }

    // --- Hino ---
    val favoriteHymnIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
        }.distinctUntilChanged()

    suspend fun addFavoriteHymn(hymnId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_HYMN_IDS] = currentFavorites + hymnId
        }
    }

    suspend fun removeFavoriteHymn(hymnId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_HYMN_IDS] = currentFavorites - hymnId
        }
    }

    fun isHymnFavoriteFlow(hymnId: String): Flow<Boolean> {
        return favoriteHymnIdsFlow.map { it.contains(hymnId) }.distinctUntilChanged()
    }

    // --- Partitura ---
    val favoriteScoreIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet()
        }.distinctUntilChanged()

    suspend fun addFavoriteScore(scoreId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_SCORE_IDS] = currentFavorites + scoreId
        }
    }

    suspend fun removeFavoriteScore(scoreId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_SCORE_IDS] = currentFavorites - scoreId
        }
    }

    fun isScoreFavoriteFlow(scoreId: String): Flow<Boolean> {
        return favoriteScoreIdsFlow.map { it.contains(scoreId) }.distinctUntilChanged()
    }

    // Função para limpar todos os favoritos (opcional, para testes ou reset)
    suspend fun clearAllFavorites() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.FAVORITE_MUSIC_IDS)
            preferences.remove(PreferencesKeys.FAVORITE_VERSE_IDS)
            preferences.remove(PreferencesKeys.FAVORITE_HYMN_IDS)
            preferences.remove(PreferencesKeys.FAVORITE_SCORE_IDS)
        }
    }
} 