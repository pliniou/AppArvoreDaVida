package com.example.apparvoredavida.data.repository.impl

import android.content.Context
import com.example.apparvoredavida.data.repository.PartituraRepository
import com.example.apparvoredavida.model.Partitura
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.util.ErrorHandler
import com.example.apparvoredavida.util.AssetNotFoundException
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
class PartituraRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetManager: AssetManager,
    private val errorHandler: ErrorHandler
) : PartituraRepository {

    private val _partiturasFavoritas = MutableStateFlow<Set<String>>(emptySet())
    private var partiturasCache: List<Partitura>? = null

    override suspend fun getPartituras(): List<Partitura> {
        try {
            // Retorna do cache se disponível
            partiturasCache?.let { return it }

            // Carrega a lista de arquivos PDF das partituras
            val pdfFiles = assetManager.listAssetFiles(Constants.DIR_PARTITURAS)
            
            // Carrega o arquivo de metadados das partituras
            val partiturasMetadata = assetManager.readJsonAsset<List<Partitura>>("${Constants.DIR_PARTITURAS}/${Constants.METADATA_FILE}")
                ?: throw AssetNotFoundException("Arquivo de metadados das partituras não encontrado")

            // Atualiza o cache e retorna
            partiturasCache = partiturasMetadata
            return partiturasMetadata
        } catch (e: Exception) {
            errorHandler.handleError(e)
            return emptyList()
        }
    }

    override suspend fun getPartituraById(id: String): Partitura? {
        return try {
            getPartituras().find { it.id == id }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            null
        }
    }

    override suspend fun searchPartituras(query: String): List<Partitura> {
        return try {
            getPartituras().filter { partitura ->
                partitura.title.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            errorHandler.handleError(e)
            emptyList()
        }
    }

    override fun observePartiturasFavoritas(): Flow<Set<String>> = _partiturasFavoritas

    override suspend fun toggleFavorito(partituraId: String, isFavorite: Boolean) {
        try {
            val currentFavorites = _partiturasFavoritas.value.toMutableSet()
            if (isFavorite) {
                currentFavorites.add(partituraId)
            } else {
                currentFavorites.remove(partituraId)
            }
            _partiturasFavoritas.value = currentFavorites
        } catch (e: Exception) {
            errorHandler.handleError(e)
        }
    }
} 