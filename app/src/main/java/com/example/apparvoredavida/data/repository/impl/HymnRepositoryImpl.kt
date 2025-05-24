package com.example.apparvoredavida.data.repository.impl

import android.content.Context
import com.example.apparvoredavida.data.repository.HymnRepository
import com.example.apparvoredavida.model.Hino
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.AssetNotFoundException
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.util.ErrorHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de hinos.
 * Gerencia o acesso aos dados dos hinos usando assets e DataStore.
 */
@Singleton
class HymnRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetManager: AssetManager,
    private val errorHandler: ErrorHandler
) : HymnRepository {

    private val _favoriteHymns = MutableStateFlow<Set<String>>(emptySet())
    private var hymnsCache: List<Hino>? = null

    override suspend fun getHymns(): List<Hino> {
        try {
            // Retorna do cache se disponível
            hymnsCache?.let { return it }

            // Carrega a lista de arquivos PDF dos hinos
            val pdfFiles = assetManager.listAssetFiles(Constants.DIR_HINARIO)
            
            // Carrega o arquivo de metadados dos hinos
            val hymnsMetadata = assetManager.readJsonAsset<List<Hino>>("${Constants.DIR_HINARIO}/metadata.json")
                ?: throw AssetNotFoundException("Arquivo de metadados dos hinos não encontrado")

            // Atualiza o cache e retorna
            hymnsCache = hymnsMetadata
            return hymnsMetadata
        } catch (e: Exception) {
            errorHandler.handleError(e)
            return emptyList()
        }
    }

    override suspend fun getHymnById(id: String): Hino? {
        return try {
            getHymns().find { it.id == id }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            null
        }
    }

    override suspend fun searchHymns(query: String): List<Hino> {
        return try {
            getHymns().filter { hymn ->
                hymn.title.contains(query, ignoreCase = true) ||
                hymn.lyrics?.contains(query, ignoreCase = true) == true
            }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            emptyList()
        }
    }

    override fun observeFavoriteHymns(): Flow<Set<String>> = _favoriteHymns

    override suspend fun toggleFavorite(hymnId: String, isFavorite: Boolean) {
        try {
            val currentFavorites = _favoriteHymns.value.toMutableSet()
            if (isFavorite) {
                currentFavorites.add(hymnId)
            } else {
                currentFavorites.remove(hymnId)
            }
            _favoriteHymns.value = currentFavorites
        } catch (e: Exception) {
            errorHandler.handleError(e)
        }
    }
} 