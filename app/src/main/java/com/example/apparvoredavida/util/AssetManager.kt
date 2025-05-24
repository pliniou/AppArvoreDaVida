package com.example.apparvoredavida.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classe utilitária para gerenciar o acesso aos assets do aplicativo.
 */
@Singleton
class AssetManager @Inject constructor(
    private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Lê um arquivo JSON dos assets e converte para o tipo especificado.
     * @param path Caminho do arquivo nos assets
     * @return Objeto deserializado ou null em caso de erro
     */
    suspend inline fun <reified T> readJsonAsset(path: String): T? = withContext(Dispatchers.IO) {
        try {
            context.assets.open(path).use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<T>(jsonString)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Lista todos os arquivos em um diretório dos assets.
     * @param path Caminho do diretório nos assets
     * @return Lista de nomes de arquivos
     */
    suspend fun listAssetFiles(path: String): List<String> = withContext(Dispatchers.IO) {
        try {
            context.assets.list(path)?.toList() ?: emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Verifica se um arquivo existe nos assets.
     * @param path Caminho do arquivo nos assets
     * @return true se o arquivo existe, false caso contrário
     */
    suspend fun assetExists(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context.assets.open(path).use { true }
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Lê o conteúdo de um arquivo dos assets como texto.
     * @param path Caminho do arquivo nos assets
     * @return Conteúdo do arquivo ou null em caso de erro
     */
    suspend fun readAssetAsText(path: String): String? = withContext(Dispatchers.IO) {
        try {
            context.assets.open(path).use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
} 