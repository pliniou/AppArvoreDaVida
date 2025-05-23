package com.example.apparvoredavida.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.apparvoredavida.model.Hino // Importar a classe Hino

@HiltViewModel
class HinarioViewModel @Inject constructor(
    // TODO: Adicionar dependências necessárias para o HinarioViewModel (ex: repositório, DataStore, etc.)
    // TODO: Injetar PdfLoader ou um repositório apropriado
    // Exemplo (substitua por sua implementação real):
    // private val pdfLoader: PdfLoader
) : ViewModel() {
    
    private val _hinos = MutableStateFlow<List<Hino>>(emptyList())
    val hinos: StateFlow<List<Hino>> = _hinos.asStateFlow()

    init {
        // TODO: Chamar loadHinos em uma coroutine
        viewModelScope.launch {
            loadHinos()
        }
    }

    // TODO: Implementar a lógica real para carregar hinos dos PDFs
    // Utilize a dependência de PdfLoader injetada para acessar os arquivos PDF dos hinos nos assets.
    // Leia os PDFs e extraia as informações relevantes (número, título, autor, etc.) para criar objetos Hino.
    // Atualize o _hinos StateFlow com a lista de Hinos carregada.
    private suspend fun loadHinos() {
        // Esta é uma implementação básica. Você precisará ler e parsear os PDFs aqui.
        // Exemplo básico de adição de hinos (remover depois):
        _hinos.value = listOf(
            Hino(id = "1", numero = 1, titulo = "Saudai o Nome de Jesus"),
            Hino(id = "2", numero = 2, titulo = "Ao Deus de Abraão Louvai"),
            Hino(id = "3", numero = 3, titulo = "Ó Adorai Sem Cessar")
        )
    }

    fun getHymnDetails(hymnId: String): Hino? {
        // Assuming hymnId is the ID used in your Hino model.
        return _hinos.value.find { it.id == hymnId }
    }

    // TODO: Remover capitalizeWords se não for mais usada (se a extração de PDFs fornecer o título formatado)
    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
} 