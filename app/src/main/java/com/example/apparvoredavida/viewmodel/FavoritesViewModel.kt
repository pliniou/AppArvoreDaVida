package com.example.apparvoredavida.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparvoredavida.data.FavoritesRepository
import com.example.apparvoredavida.model.FavoriteDisplayItem
import com.example.apparvoredavida.model.Music
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Importando as interfaces dos novos repositórios
import com.example.apparvoredavida.data.MusicRepository
import com.example.apparvoredavida.data.BibleRepository
import com.example.apparvoredavida.data.HymnRepository
import com.example.apparvoredavida.data.PartituraRepository

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    // Injetando repositórios em vez de ViewModels
    private val musicRepository: MusicRepository,
    private val bibleRepository: BibleRepository,
    private val hymnRepository: HymnRepository,
    private val partituraRepository: PartituraRepository // Usando o novo nome PartituraRepository
) : ViewModel() {

    val favoriteMusicIds: StateFlow<Set<String>> = favoritesRepository.favoriteMusicIdsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val favoriteVerseIds: StateFlow<Set<String>> = favoritesRepository.favoriteVerseIdsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val favoriteHymnIds: StateFlow<Set<String>> = favoritesRepository.favoriteHymnIdsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val favoriteScoreIds: StateFlow<Set<String>> = favoritesRepository.favoriteScoreIdsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    // Combine all favorite IDs and fetch details
    val allFavoriteItems: StateFlow<List<FavoriteDisplayItem>> = combine(
        favoriteMusicIds,
        favoriteVerseIds,
        favoriteHymnIds,
        favoriteScoreIds
    ) { musicIds, verseIds, hymnIds, scoreIds ->
        val items = mutableListOf<FavoriteDisplayItem>()

        // Fetch Music Details usando MusicRepository
        musicIds.forEach { musicId ->
            musicRepository.getMusicById(musicId)?.let { music ->
                items.add(FavoriteDisplayItem.MusicItem(musicId, music.title ?: "Unknown Title", music.artist, music))
            }
        }

        // Fetch Verse Details usando BibleRepository
        val verseItems = verseIds.mapNotNull { verseId ->
             bibleRepository.getVerseById(verseId)?.let { verseDetails ->
                 // Assuming VersiculoDetails has the info needed for title and reference
                 FavoriteDisplayItem.VerseItem(verseId, "${verseDetails.livroNome} ${verseDetails.capituloNumero}:${verseDetails.versiculoNumero}", "${verseDetails.livroNome} ${verseDetails.capituloNumero}:${verseDetails.versiculoNumero}", verseDetails)
            }
        }
        items.addAll(verseItems)

        // Fetch Hymn Details usando HymnRepository
        val hymnItems = hymnIds.mapNotNull { hymnId ->
            hymnRepository.getHymnById(hymnId)?.let { hino -> // Usar getHymnById e modelo Hino
                FavoriteDisplayItem.HymnItem(hymnId, hino.titulo, hino) // Usar campos do modelo Hino
            }
        }
        items.addAll(hymnItems)

        // Fetch Score Details usando PartituraRepository
        val scoreItems = scoreIds.mapNotNull { scoreId ->
            partituraRepository.getScoreById(scoreId)?.let { partitura -> // Usar getScoreById e modelo Partitura
                FavoriteDisplayItem.ScoreItem(scoreId, partitura.titulo, partitura) // Usar campos do modelo Partitura
            }
        }
        items.addAll(scoreItems)

        // Sort items? Maybe by type or title? For now, just return the list.
        items.toList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleFavoriteMusic(music: Music) {
        viewModelScope.launch {
            if (favoriteMusicIds.value.contains(music.id)) {
                favoritesRepository.removeFavoriteMusic(music.id)
            } else {
                favoritesRepository.addFavoriteMusic(music.id)
            }
        }
    }

    fun isMusicFavoriteFlow(musicId: String): Flow<Boolean> {
        return favoritesRepository.isMusicFavoriteFlow(musicId)
    }

    fun toggleFavoriteVerse(verseId: String) {
        viewModelScope.launch {
            if (favoriteVerseIds.value.contains(verseId)) {
                favoritesRepository.removeFavoriteVerse(verseId)
            } else {
                favoritesRepository.addFavoriteVerse(verseId)
            }
        }
    }

    fun isVerseFavoriteFlow(verseId: String): Flow<Boolean> {
        return favoritesRepository.isVerseFavoriteFlow(verseId)
    }

    // --- Hino ---
    fun toggleFavoriteHymn(hymnId: String) {
        viewModelScope.launch {
            if (favoriteHymnIds.value.contains(hymnId)) {
                favoritesRepository.removeFavoriteHymn(hymnId)
            } else {
                favoritesRepository.addFavoriteHymn(hymnId)
            }
        }
    }

    fun isHymnFavoriteFlow(hymnId: String): Flow<Boolean> {
        return favoritesRepository.isHymnFavoriteFlow(hymnId)
    }

    // --- Partitura ---
    fun toggleFavoriteScore(scoreId: String) {
        viewModelScope.launch {
            if (favoriteScoreIds.value.contains(scoreId)) {
                favoritesRepository.removeFavoriteScore(scoreId)
            } else {
                favoritesRepository.addFavoriteScore(scoreId)
            }
        }
    }

    fun isScoreFavoriteFlow(scoreId: String): Flow<Boolean> {
        return favoritesRepository.isScoreFavoriteFlow(scoreId)
    }
} 