package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.viewmodel.VisualizadorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizadorScreen(
    navController: NavController,
    fileId: String,
    fileType: String
) {
    val viewModel: VisualizadorViewModel = hiltViewModel()
    val context = LocalContext.current
    
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val currentBitmap by viewModel.currentBitmap.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(fileId, fileType) {
        viewModel.loadFile(fileId, fileType)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Visualizador",
                onBackClick = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { /* TODO: Implementar compartilhamento */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartilhar")
                    }
                    IconButton(onClick = { /* TODO: Implementar favoritos */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Adicionar aos favoritos")
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
                isLoading -> LoadingContent()
                errorMessage != null -> ErrorContent(
                    message = errorMessage ?: "Erro desconhecido",
                    onRetry = { viewModel.loadFile(fileId, fileType) }
                )
                currentBitmap != null -> PdfViewer(
                    currentBitmap = currentBitmap,
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPreviousPage = { viewModel.goToPreviousPage() },
                    onNextPage = { viewModel.goToNextPage() }
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun PdfViewer(
    currentBitmap: android.graphics.Bitmap?,
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        currentBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Página $currentPage",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Controles de navegação
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousPage,
                enabled = currentPage > 0
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                    contentDescription = "Página anterior",
                    tint = if (currentPage > 0) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            Text(
                text = "${currentPage + 1} / $totalPages",
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(
                onClick = onNextPage,
                enabled = currentPage < totalPages - 1
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = "Próxima página",
                    tint = if (currentPage < totalPages - 1) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
} 