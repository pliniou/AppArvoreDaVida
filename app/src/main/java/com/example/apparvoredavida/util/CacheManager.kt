package com.example.apparvoredavida.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

object CacheManager {
    private const val CACHE_DIR = "cache"
    private const val MAX_CACHE_SIZE = 100 * 1024 * 1024 // 100MB

    fun getCachedFile(context: Context, url: String): File? {
        val cacheDir = getCacheDir(context)
        val cacheFile = File(cacheDir, getCacheKey(url))
        return if (cacheFile.exists()) cacheFile else null
    }

    fun cacheFile(context: Context, url: String, data: ByteArray) {
        val cacheDir = getCacheDir(context)
        val cacheFile = File(cacheDir, getCacheKey(url))
        
        // Verifica tamanho do cache
        if (getCacheSize(cacheDir) + data.size > MAX_CACHE_SIZE) {
            clearOldCache(cacheDir)
        }

        // Salva arquivo
        FileOutputStream(cacheFile).use { output ->
            output.write(data)
        }
    }

    fun clearCache(context: Context) {
        getCacheDir(context).deleteRecursively()
    }

    private fun getCacheDir(context: Context): File {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
    }

    private fun getCacheKey(url: String): String {
        return MessageDigest.getInstance("MD5")
            .digest(url.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    private fun getCacheSize(cacheDir: File): Long {
        return cacheDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }

    private fun clearOldCache(cacheDir: File) {
        val files = cacheDir.listFiles()?.sortedBy { it.lastModified() } ?: return
        var freedSpace = 0L
        val targetSpace = MAX_CACHE_SIZE / 2 // Libera 50% do cache

        for (file in files) {
            if (freedSpace >= targetSpace) break
            freedSpace += file.length()
            file.delete()
        }
    }
} 