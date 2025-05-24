package com.example.apparvoredavida.data.repository

import com.example.apparvoredavida.model.Hino
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de acesso aos dados dos hinos.
 */
interface HymnRepository {
    /**
     * Obtém a lista de todos os hinos.
     */
    suspend fun getHymns(): List<Hino>

    /**
     * Obtém um hino específico pelo ID.
     */
    suspend fun getHymnById(id: String): Hino?

    /**
     * Busca hinos pelo título ou letra.
     * @param query Termo de busca
     */
    suspend fun searchHymns(query: String): List<Hino>

    /**
     * Observa a lista de hinos favoritos.
     */
    fun observeFavoriteHymns(): Flow<Set<String>>

    /**
     * Adiciona ou remove um hino dos favoritos.
     * @param hymnId ID do hino
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavorite(hymnId: String, isFavorite: Boolean)
} 