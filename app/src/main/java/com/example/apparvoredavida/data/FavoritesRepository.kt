package com.example.apparvoredavida.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório responsável por gerenciar os favoritos do usuário.
 * Utiliza DataStore para persistência dos dados.
 */
@Singleton
class FavoritesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    /**
     * Flow que emite o conjunto de IDs de músicas favoritas.
     */
    val favoriteMusicIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet()
        }.distinctUntilChanged()

    /**
     * Flow que emite o conjunto de IDs de versículos favoritos.
     */
    val favoriteVerseIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet()
        }.distinctUntilChanged()

    /**
     * Flow que emite o conjunto de IDs de hinos favoritos.
     */
    val favoriteHymnIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
        }.distinctUntilChanged()

    /**
     * Flow que emite o conjunto de IDs de partituras favoritas.
     */
    val favoriteScoreIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet()
        }.distinctUntilChanged()

    /**
     * Adiciona ou remove uma música dos favoritos.
     * @param musicId ID da música
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavoriteMusic(musicId: String, isFavorite: Boolean) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_MUSIC_IDS] = if (isFavorite) {
                currentFavorites + musicId
            } else {
                currentFavorites - musicId
            }
        }
    }

    /**
     * Adiciona ou remove um versículo dos favoritos.
     * @param verseId ID do versículo
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavoriteVerse(verseId: String, isFavorite: Boolean) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_VERSE_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_VERSE_IDS] = if (isFavorite) {
                currentFavorites + verseId
            } else {
                currentFavorites - verseId
            }
        }
    }

    /**
     * Adiciona ou remove um hino dos favoritos.
     * @param hymnId ID do hino
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavoriteHymn(hymnId: String, isFavorite: Boolean) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_HYMN_IDS] = if (isFavorite) {
                currentFavorites + hymnId
            } else {
                currentFavorites - hymnId
            }
        }
    }

    /**
     * Adiciona ou remove uma partitura dos favoritos.
     * @param scoreId ID da partitura
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavoriteScore(scoreId: String, isFavorite: Boolean) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_SCORE_IDS] ?: emptySet()
            preferences[PreferencesKeys.FAVORITE_SCORE_IDS] = if (isFavorite) {
                currentFavorites + scoreId
            } else {
                currentFavorites - scoreId
            }
        }
    }

    /**
     * Verifica se uma música está nos favoritos.
     * @param musicId ID da música
     * @return Flow que emite true se a música estiver nos favoritos
     */
    fun isMusicFavoriteFlow(musicId: String): Flow<Boolean> =
        favoriteMusicIdsFlow.map { it.contains(musicId) }.distinctUntilChanged()

    /**
     * Verifica se um versículo está nos favoritos.
     * @param verseId ID do versículo
     * @return Flow que emite true se o versículo estiver nos favoritos
     */
    fun isVerseFavoriteFlow(verseId: String): Flow<Boolean> =
        favoriteVerseIdsFlow.map { it.contains(verseId) }.distinctUntilChanged()

    /**
     * Verifica se um hino está nos favoritos.
     * @param hymnId ID do hino
     * @return Flow que emite true se o hino estiver nos favoritos
     */
    fun isHymnFavoriteFlow(hymnId: String): Flow<Boolean> =
        favoriteHymnIdsFlow.map { it.contains(hymnId) }.distinctUntilChanged()

    /**
     * Verifica se uma partitura está nos favoritos.
     * @param scoreId ID da partitura
     * @return Flow que emite true se a partitura estiver nos favoritos
     */
    fun isScoreFavoriteFlow(scoreId: String): Flow<Boolean> =
        favoriteScoreIdsFlow.map { it.contains(scoreId) }.distinctUntilChanged()

    /**
     * Limpa todos os favoritos do usuário.
     */
    suspend fun clearAllFavorites() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.FAVORITE_MUSIC_IDS)
            preferences.remove(PreferencesKeys.FAVORITE_VERSE_IDS)
            preferences.remove(PreferencesKeys.FAVORITE_HYMN_IDS)
            preferences.remove(PreferencesKeys.FAVORITE_SCORE_IDS)
        }
    }
} 