package com.example.apparvoredavida.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classe utilitária para gerenciar o cache em disco de arquivos grandes.
 * Mantém apenas métodos essenciais para salvar, ler e limpar arquivos do cache.
 */
@Singleton
class DiskCacheManager @Inject constructor(
    private val context: Context
) {
    private val cacheDir: File by lazy {
        File(context.cacheDir, Constants.DISK_CACHE_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Salva um arquivo no cache em disco.
     * @param key Chave única para o arquivo
     * @param data Dados do arquivo
     * @return true se o arquivo foi salvo com sucesso, false caso contrário
     */
    suspend fun saveToCache(key: String, data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(cacheDir, key)
            FileOutputStream(file).use { output ->
                output.write(data)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Lê um arquivo do cache em disco.
     * @param key Chave única do arquivo
     * @return Dados do arquivo ou null se não encontrado
     */
    suspend fun readFromCache(key: String): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val file = File(cacheDir, key)
            if (!file.exists()) return@withContext null
            file.readBytes()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Limpa todo o cache em disco.
     * @return true se o cache foi limpo com sucesso, false caso contrário
     */
    suspend fun clearCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            cacheDir.listFiles()?.forEach { it.delete() }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
} 