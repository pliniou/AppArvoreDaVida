package com.example.apparvoredavida.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.apparvoredavida.model.Hino
import com.example.apparvoredavida.util.PdfLoader
import android.util.Log
import com.example.apparvoredavida.util.Constants

/**
 * ViewModel responsável por gerenciar a funcionalidade do Hinário.
 * Implementa carregamento e gerenciamento de hinos a partir de arquivos PDF.
 */
@HiltViewModel
class HinarioViewModel @Inject constructor(
    private val pdfLoader: PdfLoader
) : ViewModel() {
    
    private val _hinos = MutableStateFlow<List<Hino>>(emptyList())
    val hinos: StateFlow<List<Hino>> = _hinos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            loadHinos()
        }
    }

    /**
     * Carrega os hinos a partir dos arquivos PDF.
     */
    private suspend fun loadHinos() {
        try {
            _isLoading.value = true
            _errorMessage.value = null

            // TODO: Implementar a lógica real de extração de hinos dos PDFs
            // Por enquanto, usando dados de exemplo
            _hinos.value = listOf(
                Hino(id = "hino1", numero = 1, titulo = "Saudai o Nome de Jesus"),
                Hino(id = "hino2", numero = 2, titulo = "Ao Deus de Abraão Louvai"),
                Hino(id = "hino3", numero = 3, titulo = "Ó Adorai Sem Cessar")
            )

        } catch (e: Exception) {
            Log.e("HinarioVM", "Erro ao carregar hinos: ${e.message}")
            _errorMessage.value = "Erro ao carregar hinos: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Busca os detalhes de um hino específico.
     * @param hymnId ID do hino a ser buscado
     * @return Objeto Hino ou null se não encontrado
     */
    fun getHymnDetails(hymnId: String): Hino? {
        return _hinos.value.find { it.id == hymnId }
    }

    /**
     * Carrega o PDF de um hino específico.
     * @param hymnId ID do hino a ser carregado
     * @return Bitmap da primeira página do PDF ou null em caso de erro
     */
    suspend fun loadHymnPdf(hymnId: String): ByteArray? {
        return try {
            val assetPath = "${Constants.DIR_HINARIO}/$hymnId${Constants.EXTENSION_PDF}"
            pdfLoader.loadPdfBytes(assetPath)
        } catch (e: Exception) {
            Log.e("HinarioVM", "Erro ao carregar PDF do hino $hymnId: ${e.message}")
            _errorMessage.value = "Erro ao carregar PDF do hino: ${e.message}"
            null
        }
    }
} 