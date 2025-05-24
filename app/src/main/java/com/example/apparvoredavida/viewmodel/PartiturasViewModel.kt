package com.example.apparvoredavida.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.apparvoredavida.model.Partitura
import com.example.apparvoredavida.data.repository.PartituraRepository
import com.example.apparvoredavida.data.repository.FavoritesRepository

/**
 * ViewModel responsável por gerenciar a funcionalidade de partituras.
 * Implementa carregamento e gerenciamento de partituras em PDF.
 */
@HiltViewModel
class PartiturasViewModel @Inject constructor(
    private val partituraRepository: PartituraRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _partituras = MutableStateFlow<List<Partitura>>(emptyList())
    val partituras: StateFlow<List<Partitura>> = _partituras.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            loadPartituras()
            loadFavorites()
        }
    }

    /**
     * Carrega a lista de partituras disponíveis.
     */
    private suspend fun loadPartituras() {
        try {
            _partituras.value = partituraRepository.getPartituras()
        } catch (e: Exception) {
            // TODO: Implementar tratamento de erro adequado
        }
    }

    /**
     * Carrega a lista de partituras favoritas.
     */
    private suspend fun loadFavorites() {
        try {
            favoritesRepository.favoriteScoreIdsFlow.collect { favoriteIds ->
                _favorites.value = favoriteIds
            }
        } catch (e: Exception) {
            // TODO: Implementar tratamento de erro adequado
        }
    }

    /**
     * Alterna o status de favorito de uma partitura.
     * @param partituraId ID da partitura
     */
    fun toggleFavorite(partituraId: String) {
        viewModelScope.launch {
            try {
                if (_favorites.value.contains(partituraId)) {
                    favoritesRepository.removeFavoriteScore(partituraId)
                } else {
                    favoritesRepository.addFavoriteScore(partituraId)
                }
            } catch (e: Exception) {
                // TODO: Implementar tratamento de erro adequado
            }
        }
    }

    /**
     * Verifica se uma partitura está nos favoritos.
     * @param partituraId ID da partitura
     * @return true se a partitura está nos favoritos, false caso contrário
     */
    fun isFavorite(partituraId: String): Boolean {
        return _favorites.value.contains(partituraId)
    }
} 