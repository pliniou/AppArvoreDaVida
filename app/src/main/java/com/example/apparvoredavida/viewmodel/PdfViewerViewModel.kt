package com.example.apparvoredavida.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparvoredavida.data.repository.PdfRepository
import com.example.apparvoredavida.model.PdfViewerEvent
import com.example.apparvoredavida.model.PdfViewerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val pdfRepository: PdfRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfViewerUiState())
    val uiState: StateFlow<PdfViewerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PdfViewerEvent>()
    val events: SharedFlow<PdfViewerEvent> = _events.asSharedFlow()

    private var currentPdfName: String? = null

    fun loadPdf(pdfName: String) {
        currentPdfName = pdfName
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val renderer = pdfRepository.loadPdfFromAsset(pdfName)
                val pageCount = renderer.pageCount
                _uiState.update { 
                    it.copy(
                        pageCount = pageCount,
                        isLoading = false
                    )
                }
                loadLastViewedPage(pdfName)
                _events.emit(PdfViewerEvent.PdfLoaded)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar PDF: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadLastViewedPage(pdfName: String) {
        viewModelScope.launch {
            pdfRepository.getLastViewedPage(pdfName).collect { pageIndex ->
                if (pageIndex in 0 until _uiState.value.pageCount) {
                    navigateToPage(pageIndex)
                }
            }
        }
    }

    fun navigateToPage(pageIndex: Int) {
        if (pageIndex !in 0 until _uiState.value.pageCount) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isPageLoading = true) }
            try {
                val bitmap = pdfRepository.renderPage(pageIndex, 1080, 1920)
                _uiState.update { 
                    it.copy(
                        currentPageIndex = pageIndex,
                        currentPageBitmap = bitmap.asImageBitmap(),
                        isPageLoading = false
                    )
                }
                currentPdfName?.let { pdfName ->
                    pdfRepository.saveLastViewedPage(pdfName, pageIndex)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isPageLoading = false,
                        errorMessage = "Erro ao renderizar página: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateZoomLevel(zoomLevel: Float) {
        _uiState.update { it.copy(zoomLevel = zoomLevel) }
        currentPdfName?.let { pdfName ->
            viewModelScope.launch {
                pdfRepository.saveZoomLevel(pdfName, zoomLevel)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pdfRepository.closeCurrentPdf()
    }
} 