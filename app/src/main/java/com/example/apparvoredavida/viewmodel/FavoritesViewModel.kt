package com.example.apparvoredavida.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparvoredavida.data.repository.FavoritesRepository
import com.example.apparvoredavida.model.FavoriteDisplayItem
import com.example.apparvoredavida.model.Music
import com.example.apparvoredavida.model.VerseDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Importando as interfaces dos repositórios
import com.example.apparvoredavida.data.repository.MusicRepository
import com.example.apparvoredavida.data.repository.BibleRepository
import com.example.apparvoredavida.data.repository.HymnRepository
import com.example.apparvoredavida.data.repository.PartituraRepository

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val musicRepository: MusicRepository,
    private val bibleRepository: BibleRepository,
    private val hymnRepository: HymnRepository,
    private val partituraRepository: PartituraRepository
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
                 FavoriteDisplayItem.VerseItem(verseId, "${verseDetails.livroNome} ${verseDetails.capituloNumero}:${verseDetails.versiculoNumero}", "${verseDetails.livroNome} ${verseDetails.capituloNumero}:${verseDetails.versiculoNumero}", verseDetails)
            }
        }
        items.addAll(verseItems)

        // Fetch Hymn Details usando HymnRepository
        val hymnItems = hymnIds.mapNotNull { hymnId ->
            hymnRepository.getHymnById(hymnId)?.let { hino ->
                FavoriteDisplayItem.HymnItem(hymnId, hino.titulo, hino)
            }
        }
        items.addAll(hymnItems)

        // Fetch Score Details usando PartituraRepository
        val scoreItems = scoreIds.mapNotNull { scoreId ->
            partituraRepository.getPartituraById(scoreId)?.let { partitura ->
                FavoriteDisplayItem.ScoreItem(scoreId, partitura.title, partitura)
            }
        }
        items.addAll(scoreItems)

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