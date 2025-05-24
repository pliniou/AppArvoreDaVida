package com.example.apparvoredavida.data.repository.impl

import android.content.Context
import com.example.apparvoredavida.data.repository.MusicRepository
import com.example.apparvoredavida.model.Album
import com.example.apparvoredavida.model.Music
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.CacheManager
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.util.ErrorHandler
import com.example.apparvoredavida.util.AssetNotFoundException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de músicas.
 * Gerencia o acesso aos dados das músicas usando assets e DataStore.
 */
@Singleton
class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetManager: AssetManager,
    private val cacheManager: CacheManager,
    private val errorHandler: ErrorHandler
) : MusicRepository {

    private val _favoriteMusics = MutableStateFlow<Set<String>>(emptySet())
    private var musicsCache: List<Music>? = null
    private var albumsCache: List<Album>? = null

    override suspend fun getMusics(): List<Music> {
        try {
            // Retorna do cache se disponível
            musicsCache?.let { return it }

            // Carrega a lista de arquivos MP3 das músicas
            val mp3Files = assetManager.listAssetFiles(Constants.DIR_MUSICAS)
            
            // Carrega o arquivo de metadados das músicas
            val musicsMetadata = assetManager.readJsonAsset<List<Music>>("${Constants.DIR_MUSICAS}/${Constants.METADATA_FILE}")
                ?: throw AssetNotFoundException("Arquivo de metadados das músicas não encontrado")

            // Atualiza o cache e retorna
            musicsCache = musicsMetadata
            return musicsMetadata
        } catch (e: Exception) {
            errorHandler.handleError(e)
            return emptyList()
        }
    }

    override suspend fun getMusicById(id: String): Music? {
        return try {
            getMusics().find { it.id == id }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            null
        }
    }

    override suspend fun searchMusics(query: String): List<Music> {
        return try {
            getMusics().filter { music ->
                music.title.contains(query, ignoreCase = true) ||
                music.artist?.contains(query, ignoreCase = true) == true
            }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            emptyList()
        }
    }

    override suspend fun getAlbums(): List<Album> {
        try {
            // Retorna do cache se disponível
            albumsCache?.let { return it }

            // Carrega o arquivo de metadados dos álbuns
            val albumsMetadata = assetManager.readJsonAsset<List<Album>>("${Constants.DIR_MUSICAS}/${Constants.ALBUMS_FILE}")
                ?: throw AssetNotFoundException("Arquivo de metadados dos álbuns não encontrado")

            // Atualiza o cache e retorna
            albumsCache = albumsMetadata
            return albumsMetadata
        } catch (e: Exception) {
            errorHandler.handleError(e)
            return emptyList()
        }
    }

    override suspend fun getAlbumById(id: String): Album? {
        return try {
            getAlbums().find { it.id == id }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            null
        }
    }

    override suspend fun getAlbumMusics(albumId: String): List<Music> {
        return try {
            getMusics().filter { it.album == albumId }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            emptyList()
        }
    }

    override fun observeFavoriteMusics(): Flow<Set<String>> = _favoriteMusics

    override suspend fun toggleFavorite(musicId: String, isFavorite: Boolean) {
        try {
            val currentFavorites = _favoriteMusics.value.toMutableSet()
            if (isFavorite) {
                currentFavorites.add(musicId)
            } else {
                currentFavorites.remove(musicId)
            }
            _favoriteMusics.value = currentFavorites
        } catch (e: Exception) {
            errorHandler.handleError(e)
        }
    }
} 