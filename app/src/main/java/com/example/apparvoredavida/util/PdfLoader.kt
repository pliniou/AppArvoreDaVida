package com.example.apparvoredavida.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PdfLoader(private val context: Context) {
    private val cacheDir = File(context.cacheDir, "pdf_cache").apply { mkdirs() }
    private val pageCache = mutableMapOf<String, MutableMap<Int, Bitmap>>()

    suspend fun loadPdfPage(assetPath: String, pageNumber: Int, width: Int): Bitmap? = withContext(Dispatchers.IO) {
        val cacheKey = "${assetPath}_${pageNumber}_${width}"
        
        // Verifica cache em memória
        pageCache[assetPath]?.get(pageNumber)?.let { return@withContext it }
        
        // Verifica cache em disco
        val cachedFile = File(cacheDir, cacheKey)
        if (cachedFile.exists()) {
            val bitmap = android.graphics.BitmapFactory.decodeFile(cachedFile.absolutePath)
            pageCache.getOrPut(assetPath) { mutableMapOf() }[pageNumber] = bitmap
            return@withContext bitmap
        }

        // Carrega do asset
        try {
            val inputStream = context.assets.open(assetPath)
            val tempFile = File.createTempFile("temp_pdf", null, context.cacheDir)
            FileOutputStream(tempFile).use { it.write(inputStream.readBytes()) }
            
            val fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            
            pdfRenderer.openPage(pageNumber).use { page ->
                val bitmap = Bitmap.createBitmap(width, (width * page.height / page.width.toFloat()).toInt(), Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                
                // Salva no cache
                FileOutputStream(cachedFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                
                pageCache.getOrPut(assetPath) { mutableMapOf() }[pageNumber] = bitmap
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearCache() {
        pageCache.clear()
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    fun clearPageCache(assetPath: String) {
        pageCache.remove(assetPath)
        cacheDir.listFiles()?.filter { it.name.startsWith(assetPath) }?.forEach { it.delete() }
    }
} 