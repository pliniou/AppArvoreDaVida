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
import android.util.Log

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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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
            _isLoading.value = true
            _errorMessage.value = null
            _partituras.value = partituraRepository.getPartituras()
        } catch (e: Exception) {
            Log.e("PartiturasVM", "Erro ao carregar partituras: ${e.message}")
            _errorMessage.value = "Erro ao carregar partituras: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Carrega a lista de partituras favoritas.
     */
    private suspend fun loadFavorites() {
        try {
            _isLoading.value = true
            _errorMessage.value = null
            favoritesRepository.favoriteScoreIdsFlow.collect { favoriteIds ->
                _favorites.value = favoriteIds
            }
        } catch (e: Exception) {
            Log.e("PartiturasVM", "Erro ao carregar favoritos: ${e.message}")
            _errorMessage.value = "Erro ao carregar favoritos: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Alterna o status de favorito de uma partitura.
     * @param partituraId ID da partitura
     */
    fun toggleFavorite(partituraId: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                if (_favorites.value.contains(partituraId)) {
                    favoritesRepository.removeFavoriteScore(partituraId)
                } else {
                    favoritesRepository.addFavoriteScore(partituraId)
                }
            } catch (e: Exception) {
                Log.e("PartiturasVM", "Erro ao alternar favorito: ${e.message}")
                _errorMessage.value = "Erro ao alternar favorito: ${e.message}"
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