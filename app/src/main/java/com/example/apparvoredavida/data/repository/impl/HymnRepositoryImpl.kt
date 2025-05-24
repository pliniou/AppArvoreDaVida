package com.example.apparvoredavida.data.repository.impl

import android.content.Context
import com.example.apparvoredavida.data.repository.HymnRepository
import com.example.apparvoredavida.model.Hino
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.Constants
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
    private val assetManager: AssetManager
) : HymnRepository {

    private val _favoriteHymns = MutableStateFlow<Set<String>>(emptySet())
    private var hymnsCache: List<Hino>? = null

    override suspend fun getHymns(): List<Hino> {
        // Retorna do cache se disponível
        hymnsCache?.let { return it }

        // Carrega a lista de arquivos PDF dos hinos
        val pdfFiles = assetManager.listAssetFiles(Constants.DIR_HINARIO)
        
        // Carrega o arquivo de metadados dos hinos
        val hymnsMetadata = assetManager.readJsonAsset<List<Hino>>("${Constants.DIR_HINARIO}/metadata.json")
            ?: return emptyList()

        // Atualiza o cache e retorna
        hymnsCache = hymnsMetadata
        return hymnsMetadata
    }

    override suspend fun getHymnById(id: String): Hino? {
        return getHymns().find { it.id == id }
    }

    override suspend fun searchHymns(query: String): List<Hino> {
        return getHymns().filter { hymn ->
            hymn.title.contains(query, ignoreCase = true) ||
            hymn.lyrics?.contains(query, ignoreCase = true) == true
        }
    }

    override fun observeFavoriteHymns(): Flow<Set<String>> = _favoriteHymns

    override suspend fun toggleFavorite(hymnId: String, isFavorite: Boolean) {
        val currentFavorites = _favoriteHymns.value.toMutableSet()
        if (isFavorite) {
            currentFavorites.add(hymnId)
        } else {
            currentFavorites.remove(hymnId)
        }
        _favoriteHymns.value = currentFavorites
    }
} 