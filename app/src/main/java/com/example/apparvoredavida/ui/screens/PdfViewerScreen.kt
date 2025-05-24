package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apparvoredavida.model.PdfViewerEvent
import com.example.apparvoredavida.ui.components.PdfPageNavigator
import com.example.apparvoredavida.viewmodel.PdfViewerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    pdfName: String,
    onNavigateBack: () -> Unit,
    viewModel: PdfViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pdfName) {
        viewModel.loadPdf(pdfName)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PdfViewerEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is PdfViewerEvent.NavigateToPage -> {
                    viewModel.navigateToPage(event.pageIndex)
                }
                PdfViewerEvent.PdfLoaded -> {
                    // PDF carregado com sucesso
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pdfName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            uiState.currentPageBitmap?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Página ${uiState.currentPageIndex + 1}",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(
                                            scaleX = uiState.zoomLevel,
                                            scaleY = uiState.zoomLevel
                                        )
                                        .pointerInput(Unit) {
                                            detectTransformGestures { _, pan, zoom, _ ->
                                                viewModel.updateZoomLevel(
                                                    (uiState.zoomLevel * zoom).coerceIn(0.5f, 3f)
                                                )
                                            }
                                        },
                                    contentScale = ContentScale.Fit
                                )
                            }

                            if (uiState.isPageLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        PdfPageNavigator(
                            currentPage = uiState.currentPageIndex,
                            totalPages = uiState.pageCount,
                            onPreviousPage = {
                                if (uiState.currentPageIndex > 0) {
                                    viewModel.navigateToPage(uiState.currentPageIndex - 1)
                                }
                            },
                            onNextPage = {
                                if (uiState.currentPageIndex < uiState.pageCount - 1) {
                                    viewModel.navigateToPage(uiState.currentPageIndex + 1)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
} 