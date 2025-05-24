package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apparvoredavida.ui.components.AppPdfViewer
import com.example.apparvoredavida.viewmodel.PdfViewerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    pdfName: String,
    onNavigateBack: () -> Unit,
    viewModel: PdfViewerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var isLoadingError by remember { mutableStateOf(false) }

    LaunchedEffect(pdfName) {
        viewModel.loadPdf(pdfName)?.let { file ->
            pdfFile = file
        } ?: run {
            isLoadingError = true
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                pdfFile != null -> {
                    AppPdfViewer(
                        modifier = Modifier.fillMaxSize(),
                        file = pdfFile!!,
                        onClose = onNavigateBack,
                        initialPage = viewModel.getLastViewedPage(pdfName),
                        onPageChanged = { page -> viewModel.saveLastViewedPage(pdfName, page) }
                    )
                }
                isLoadingError -> {
                    Text(
                        text = "Erro ao carregar o PDF",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
} 