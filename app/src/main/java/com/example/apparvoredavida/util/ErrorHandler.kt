package com.example.apparvoredavida.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classe utilitária para gerenciar erros do aplicativo.
 */
@Singleton
class ErrorHandler @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "ErrorHandler"
    }

    /**
     * Handler de exceções para coroutines.
     */
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    /**
     * Trata um erro ocorrido no aplicativo.
     * @param throwable Exceção a ser tratada
     */
    fun handleError(throwable: Throwable) {
        when (throwable) {
            is AssetNotFoundException -> {
                Log.e(TAG, "Asset não encontrado: ${throwable.message}")
                // TODO: Implementar notificação ao usuário
            }
            is CacheException -> {
                Log.e(TAG, "Erro no cache: ${throwable.message}")
                // TODO: Implementar notificação ao usuário
            }
            is NetworkException -> {
                Log.e(TAG, "Erro de rede: ${throwable.message}")
                // TODO: Implementar notificação ao usuário
            }
            else -> {
                Log.e(TAG, "Erro não tratado: ${throwable.message}", throwable)
                // TODO: Implementar notificação ao usuário
            }
        }
    }
}

/**
 * Exceção lançada quando um asset não é encontrado.
 */
class AssetNotFoundException(message: String) : Exception(message)

/**
 * Exceção lançada quando ocorre um erro no cache.
 */
class CacheException(message: String) : Exception(message)

/**
 * Exceção lançada quando ocorre um erro de rede.
 */
class NetworkException(message: String) : Exception(message) 