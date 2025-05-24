package com.example.apparvoredavida.data.repository

import com.example.apparvoredavida.model.Partitura
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de acesso aos dados das partituras.
 */
interface ScoreRepository {
    /**
     * Obtém a lista de todas as partituras.
     */
    suspend fun getScores(): List<Partitura>

    /**
     * Obtém uma partitura específica pelo ID.
     */
    suspend fun getScoreById(id: String): Partitura?

    /**
     * Busca partituras pelo título.
     * @param query Termo de busca
     */
    suspend fun searchScores(query: String): List<Partitura>

    /**
     * Observa a lista de partituras favoritas.
     */
    fun observeFavoriteScores(): Flow<Set<String>>

    /**
     * Adiciona ou remove uma partitura dos favoritos.
     * @param scoreId ID da partitura
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavorite(scoreId: String, isFavorite: Boolean)
} 