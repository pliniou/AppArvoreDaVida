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
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel responsável por gerenciar a visualização de arquivos PDF.
 * Implementa funcionalidades para carregar PDFs e gerenciar preferências de visualização.
 */
@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfPreferences: PdfPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentPdfFile = MutableStateFlow<File?>(null)
    val currentPdfFile: StateFlow<File?> = _currentPdfFile.asStateFlow()

    suspend fun loadPdf(pdfName: String): File? {
        return try {
            _isLoading.value = true
            _errorMessage.value = null

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
            
            _currentPdfFile.value = tempFile
            tempFile
        } catch (e: Exception) {
            Log.e("PdfViewerVM", "Erro ao carregar PDF: ${e.message}")
            _errorMessage.value = "Erro ao carregar PDF: ${e.message}"
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun getLastViewedPage(pdfName: String): Int {
        return try {
            _errorMessage.value = null
            pdfPreferences.getLastViewedPage(pdfName).first()
        } catch (e: Exception) {
            Log.e("PdfViewerVM", "Erro ao obter última página: ${e.message}")
            _errorMessage.value = "Erro ao obter última página: ${e.message}"
            0
        }
    }

    fun saveLastViewedPage(pdfName: String, pageIndex: Int) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                pdfPreferences.saveLastViewedPage(pdfName, pageIndex)
            } catch (e: Exception) {
                Log.e("PdfViewerVM", "Erro ao salvar última página: ${e.message}")
                _errorMessage.value = "Erro ao salvar última página: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Limpa arquivos temporários
        try {
            context.cacheDir.listFiles()?.filter { it.name.startsWith("pdf_viewer_") }?.forEach { it.delete() }
            _currentPdfFile.value = null
        } catch (e: Exception) {
            Log.e("PdfViewerVM", "Erro ao limpar arquivos temporários: ${e.message}")
        }
    }
} 