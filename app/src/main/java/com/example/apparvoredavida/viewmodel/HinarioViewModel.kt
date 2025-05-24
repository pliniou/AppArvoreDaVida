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

    init {
        viewModelScope.launch {
            loadHinos()
        }
    }

    /**
     * Carrega os hinos a partir dos arquivos PDF.
     * TODO: Implementar a lógica real de extração de hinos dos PDFs.
     */
    private suspend fun loadHinos() {
        // Implementação temporária com dados de exemplo
        _hinos.value = listOf(
            Hino(id = "1", numero = 1, titulo = "Saudai o Nome de Jesus"),
            Hino(id = "2", numero = 2, titulo = "Ao Deus de Abraão Louvai"),
            Hino(id = "3", numero = 3, titulo = "Ó Adorai Sem Cessar")
        )
    }

    /**
     * Busca os detalhes de um hino específico.
     * @param hymnId ID do hino a ser buscado
     * @return Objeto Hino ou null se não encontrado
     */
    fun getHymnDetails(hymnId: String): Hino? {
        return _hinos.value.find { it.id == hymnId }
    }

    // TODO: Remover capitalizeWords se não for mais usada (se a extração de PDFs fornecer o título formatado)
    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
} 