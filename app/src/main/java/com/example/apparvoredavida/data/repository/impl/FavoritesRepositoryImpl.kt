package com.example.apparvoredavida.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.apparvoredavida.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FavoritesRepository {

    private object PreferencesKeys {
        val FAVORITE_MUSIC_IDS = stringSetPreferencesKey("favorite_music_ids")
        val FAVORITE_VERSE_IDS = stringSetPreferencesKey("favorite_verse_ids")
        val FAVORITE_HYMN_IDS = stringSetPreferencesKey("favorite_hymn_ids")
        val FAVORITE_SCORE_IDS = stringSetPreferencesKey("favorite_score_ids")
    }

    override val favoriteMusicIdsFlow: Flow<Set<String>> = dataStore.data
        .map { it[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet() }

    override val favoriteVerseIdsFlow: Flow<Set<String>> = dataStore.data
        .map { it[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet() }

    override val favoriteHymnIdsFlow: Flow<Set<String>> = dataStore.data
        .map { it[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet() }

    override val favoriteScoreIdsFlow: Flow<Set<String>> = dataStore.data
        .map { it[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet() }

    override suspend fun addFavoriteMusic(musicId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_MUSIC_IDS] = currentFavorites + musicId
        }
    }

    override suspend fun removeFavoriteMusic(musicId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_MUSIC_IDS] = currentFavorites - musicId
        }
    }

    override suspend fun addFavoriteVerse(verseId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_VERSE_IDS] = currentFavorites + verseId
        }
    }

    override suspend fun removeFavoriteVerse(verseId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_VERSE_IDS] = currentFavorites - verseId
        }
    }

    override suspend fun addFavoriteHymn(hymnId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_HYMN_IDS] = currentFavorites + hymnId
        }
    }

    override suspend fun removeFavoriteHymn(hymnId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_HYMN_IDS] = currentFavorites - hymnId
        }
    }

    override suspend fun addFavoriteScore(scoreId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_SCORE_IDS] = currentFavorites + scoreId
        }
    }

    override suspend fun removeFavoriteScore(scoreId: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet()
            prefs[PreferencesKeys.FAVORITE_SCORE_IDS] = currentFavorites - scoreId
        }
    }

    override fun isMusicFavoriteFlow(musicId: String): Flow<Boolean> =
        favoriteMusicIdsFlow.map { it.contains(musicId) }

    override fun isVerseFavoriteFlow(verseId: String): Flow<Boolean> =
        favoriteVerseIdsFlow.map { it.contains(verseId) }

    override fun isHymnFavoriteFlow(hymnId: String): Flow<Boolean> =
        favoriteHymnIdsFlow.map { it.contains(hymnId) }

    override fun isScoreFavoriteFlow(scoreId: String): Flow<Boolean> =
        favoriteScoreIdsFlow.map { it.contains(scoreId) }
} 