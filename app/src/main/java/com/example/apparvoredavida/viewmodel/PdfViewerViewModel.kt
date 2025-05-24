package com.example.apparvoredavida.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparvoredavida.data.datastore.PdfPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfPreferences: PdfPreferences
) : ViewModel() {

    suspend fun loadPdf(pdfName: String): File? {
        return try {
            val inputStream = context.assets.open(pdfName)
            val tempFile = File.createTempFile(
                "pdf_viewer_",
                ".pdf",
                context.cacheDir
            )
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLastViewedPage(pdfName: String): Int {
        return pdfPreferences.getLastViewedPage(pdfName).first()
    }

    fun saveLastViewedPage(pdfName: String, pageIndex: Int) {
        viewModelScope.launch {
            pdfPreferences.saveLastViewedPage(pdfName, pageIndex)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Limpa arquivos temporários
        context.cacheDir.listFiles()?.filter { it.name.startsWith("pdf_viewer_") }?.forEach { it.delete() }
    }
} 