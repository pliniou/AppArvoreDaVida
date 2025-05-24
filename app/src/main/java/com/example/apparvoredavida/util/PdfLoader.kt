package com.example.apparvoredavida.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classe utilitária para carregar e renderizar páginas de PDF.
 * Gerencia o cache de arquivos PDF e a renderização de páginas.
 */
@Singleton
class PdfLoader @Inject constructor(
    private val context: Context
) {
    private val cacheDir = File(context.cacheDir, Constants.PDF_CACHE_DIR).apply {
        if (!exists()) mkdirs()
    }

    /**
     * Carrega uma página específica de um PDF do assets.
     * @param assetPath Caminho do arquivo no assets
     * @param pageIndex Índice da página (começando em 0)
     * @param targetWidth Largura alvo para renderização
     * @return Bitmap da página renderizada ou null se houver erro
     */
    fun loadPdfPage(assetPath: String, pageIndex: Int, targetWidth: Int): Bitmap? {
        return try {
            val cacheFile = getCacheFile(assetPath)
            if (!cacheFile.exists()) {
                copyAssetToCache(assetPath, cacheFile)
            }

            val fileDescriptor = ParcelFileDescriptor.open(
                cacheFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            PdfRenderer(fileDescriptor).use { renderer ->
                if (pageIndex < 0 || pageIndex >= renderer.pageCount) {
                    return null
                }

                renderer.openPage(pageIndex).use { page ->
                    val scale = targetWidth.toFloat() / page.width
                    val bitmap = Bitmap.createBitmap(
                        (page.width * scale).toInt(),
                        (page.height * scale).toInt(),
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmap
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Limpa o cache de PDFs.
     */
    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    private fun getCacheFile(assetPath: String): File {
        val fileName = assetPath.substringAfterLast("/")
        return File(cacheDir, fileName)
    }

    private fun copyAssetToCache(assetPath: String, cacheFile: File) {
        context.assets.open(assetPath).use { input ->
            FileOutputStream(cacheFile).use { output ->
                input.copyTo(output)
            }
        }
    }
} 