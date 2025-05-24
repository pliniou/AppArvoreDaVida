package com.example.apparvoredavida.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Classe que representa o estado da UI.
 *
 * @param T Tipo de dados do estado
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retry: (() -> Unit)? = null) : UiState<Nothing>()
}

/**
 * Classe para gerenciar o estado da UI.
 *
 * @param T Tipo de dados do estado
 */
class UiStateManager<T> {
    private val _state = MutableStateFlow<UiState<T>>(UiState.Loading)
    val state: StateFlow<UiState<T>> = _state.asStateFlow()

    /**
     * Atualiza o estado para loading.
     */
    fun setLoading() {
        _state.value = UiState.Loading
    }

    /**
     * Atualiza o estado para sucesso com os dados.
     *
     * @param data Dados a serem exibidos
     */
    fun setSuccess(data: T) {
        _state.value = UiState.Success(data)
    }

    /**
     * Atualiza o estado para erro.
     *
     * @param message Mensagem de erro
     * @param retry Função opcional para tentar novamente
     */
    fun setError(message: String, retry: (() -> Unit)? = null) {
        _state.value = UiState.Error(message, retry)
    }
} 