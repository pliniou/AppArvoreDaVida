package com.example.apparvoredavida.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.apparvoredavida.data.datastore.PdfPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "pdf_preferences")

@Singleton
class PdfRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfPreferences: PdfPreferences
) {
    private var currentRenderer: PdfRenderer? = null
    private var currentDescriptor: ParcelFileDescriptor? = null

    suspend fun loadPdfFromAsset(assetFileName: String): PdfRenderer {
        closeCurrentPdf()
        val parcelFileDescriptor = context.assets.openFd(assetFileName).parcelFileDescriptor
        currentDescriptor = parcelFileDescriptor
        currentRenderer = PdfRenderer(parcelFileDescriptor)
        return currentRenderer!!
    }

    fun renderPage(pageIndex: Int, width: Int, height: Int): Bitmap {
        val renderer = currentRenderer ?: throw IllegalStateException("PDF não carregado")
        val page = renderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        return bitmap
    }

    fun getPageCount(): Int = currentRenderer?.pageCount ?: 0

    fun closeCurrentPdf() {
        currentRenderer?.close()
        currentDescriptor?.close()
        currentRenderer = null
        currentDescriptor = null
    }

    // Preferências do usuário
    suspend fun saveLastViewedPage(pdfName: String, pageIndex: Int) {
        pdfPreferences.saveLastViewedPage(pdfName, pageIndex)
    }

    fun getLastViewedPage(pdfName: String): Flow<Int> {
        return pdfPreferences.getLastViewedPage(pdfName)
    }

    suspend fun saveZoomLevel(pdfName: String, zoomLevel: Float) {
        pdfPreferences.saveZoomLevel(pdfName, zoomLevel)
    }

    fun getZoomLevel(pdfName: String): Flow<Float> {
        return pdfPreferences.getZoomLevel(pdfName)
    }
} 