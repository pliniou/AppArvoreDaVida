package com.example.apparvoredavida.data.repository

import com.example.apparvoredavida.model.Album
import com.example.apparvoredavida.model.Music
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de acesso aos dados das músicas.
 */
interface MusicRepository {
    /**
     * Obtém a lista de todas as músicas.
     */
    suspend fun getMusics(): List<Music>

    /**
     * Obtém uma música específica pelo ID.
     */
    suspend fun getMusicById(id: String): Music?

    /**
     * Busca músicas pelo título ou artista.
     * @param query Termo de busca
     */
    suspend fun searchMusics(query: String): List<Music>

    /**
     * Obtém a lista de álbuns.
     */
    suspend fun getAlbums(): List<Album>

    /**
     * Obtém um álbum específico pelo ID.
     */
    suspend fun getAlbumById(id: String): Album?

    /**
     * Obtém as músicas de um álbum específico.
     */
    suspend fun getAlbumMusics(albumId: String): List<Music>

    /**
     * Observa a lista de músicas favoritas.
     */
    fun observeFavoriteMusics(): Flow<Set<String>>

    /**
     * Adiciona ou remove uma música dos favoritos.
     * @param musicId ID da música
     * @param isFavorite true para adicionar, false para remover
     */
    suspend fun toggleFavorite(musicId: String, isFavorite: Boolean)
} 