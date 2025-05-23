package com.example.apparvoredavida.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class VisualizadorViewModel @Inject constructor(
    // Dependências como PdfLoader ou repositório de favoritos podem ser injetadas aqui
) : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(0)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Função para atualizar a página atual (chamada do listener da View)
    fun updateCurrentPage(page: Int) {
        _currentPage.value = page
    }

    // Função para atualizar o total de páginas (chamada do listener da View)
    fun updateTotalPages(total: Int) {
        _totalPages.value = total
    }

    // Função para definir mensagem de erro
    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    // TODO: Implementar lógica para carregar PDF (usando PdfLoader, etc.)
    // TODO: Implementar lógica para favoritar/compartilhar (usando repositórios)

    // TODO: Implementar função para compartilhar o PDF atual
    fun sharePdf(filePath: String) {
        // Lógica para compartilhar o arquivo PDF
    }

    // TODO: Implementar função para adicionar/remover o PDF atual dos favoritos
    fun toggleFavorite(filePath: String) {
        // Lógica para adicionar ou remover o PDF dos favoritos (usando DataStore/repositório)
    }

    // Função para navegar para a próxima página
    fun goToNextPage() {
        if (_currentPage.value < _totalPages.value - 1) {
            _currentPage.value++
        }
    }

    // Função para navegar para a página anterior
    fun goToPreviousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }
} 