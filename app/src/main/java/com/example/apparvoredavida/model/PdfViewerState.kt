package com.example.apparvoredavida.model

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Representa o estado da UI do visualizador de PDF
 */
data class PdfViewerUiState(
    val isLoading: Boolean = false,
    val pageCount: Int = 0,
    val currentPageIndex: Int = 0,
    val currentPageBitmap: ImageBitmap? = null,
    val errorMessage: String? = null,
    val zoomLevel: Float = 1f,
    val isPageLoading: Boolean = false
)

/**
 * Eventos pontuais do visualizador de PDF
 */
sealed class PdfViewerEvent {
    data class ShowMessage(val message: String) : PdfViewerEvent()
    data class NavigateToPage(val pageIndex: Int) : PdfViewerEvent()
    object PdfLoaded : PdfViewerEvent()
} 