package com.example.apparvoredavida.data.repository

import com.example.apparvoredavida.model.Partitura
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de acesso aos dados das partituras.
 */
interface PartituraRepository {
    /**
     * Obtém a lista de todas as partituras.
     */
    suspend fun getPartituras(): List<Partitura>

    /**
     * Obtém uma partitura específica pelo ID.
     */
    suspend fun getPartituraById(id: String): Partitura?

    /**
     * Busca partituras pelo título.
     * @param query Termo de busca
     */
    suspend fun searchPartituras(query: String): List<Partitura>

    /**
     * Observa a lista de partituras favoritas.
     */
    fun observePartiturasFavoritas(): Flow<Set<String>>

    /**
     * Adiciona ou remove uma partitura dos favoritos.
     * @param partituraId ID da partitura
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavorito(partituraId: String, isFavorite: Boolean)
} 