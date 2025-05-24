package com.example.apparvoredavida.data.repository.impl

import android.content.Context
import com.example.apparvoredavida.data.repository.ScoreRepository
import com.example.apparvoredavida.model.Partitura
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de partituras.
 * Gerencia o acesso aos dados das partituras usando assets e DataStore.
 */
@Singleton
class ScoreRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetManager: AssetManager
) : ScoreRepository {

    private val _favoriteScores = MutableStateFlow<Set<String>>(emptySet())
    private var scoresCache: List<Partitura>? = null

    override suspend fun getScores(): List<Partitura> {
        // Retorna do cache se disponível
        scoresCache?.let { return it }

        // Carrega a lista de arquivos PDF das partituras
        val pdfFiles = assetManager.listAssetFiles(Constants.DIR_PARTITURAS)
        
        // Carrega o arquivo de metadados das partituras
        val scoresMetadata = assetManager.readJsonAsset<List<Partitura>>("${Constants.DIR_PARTITURAS}/metadata.json")
            ?: return emptyList()

        // Atualiza o cache e retorna
        scoresCache = scoresMetadata
        return scoresMetadata
    }

    override suspend fun getScoreById(id: String): Partitura? {
        return getScores().find { it.id == id }
    }

    override suspend fun searchScores(query: String): List<Partitura> {
        return getScores().filter { score ->
            score.title.contains(query, ignoreCase = true)
        }
    }

    override fun observeFavoriteScores(): Flow<Set<String>> = _favoriteScores

    override suspend fun toggleFavorite(scoreId: String, isFavorite: Boolean) {
        val currentFavorites = _favoriteScores.value.toMutableSet()
        if (isFavorite) {
            currentFavorites.add(scoreId)
        } else {
            currentFavorites.remove(scoreId)
        }
        _favoriteScores.value = currentFavorites
    }
} 