package com.example.apparvoredavida.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.util.AssetManager
import java.io.File
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.Image
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import com.jidogoon.pdfrendererview.PdfRendererView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.jidogoon.pdfrendererview.Quality
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apparvoredavida.viewmodel.VisualizadorViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizadorScreen(
    navController: NavController,
    filePath: String,
    fileType: String,
    viewModel: VisualizadorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isZoomed by remember { mutableStateOf(false) }
    var pdfRendererView: PdfRendererView? by remember { mutableStateOf(null) }

    // Observar estado do ViewModel
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = when (fileType) {
                "pdf" -> "Partitura"
                "mp3" -> "Música"
                else -> "Visualizador"
            },
            onBackClick = { navController.navigateUp() },
            actions = {
                IconButton(onClick = { viewModel.sharePdf(filePath) /* TODO: Implementar compartilhamento */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Compartilhar")
                }
                IconButton(onClick = { viewModel.toggleFavorite(filePath) /* TODO: Implementar favoritos */ }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favoritar")
                }
            }
        )

        when (fileType) {
            "pdf" -> {
                // Construir o URI para o asset
                val pdfUri = "file:///android_asset/" + filePath

                AndroidView(
                    factory = {
                        PdfRendererView(it).apply {
                            pdfRendererView = this
                            // Configurar a PdfRendererView com o URI do asset
                            initWithUrl(pdfUri, quality = Quality.NORMAL)

                            // Configurar o StatusCallBack
                            statusListener = object : PdfRendererView.StatusCallBack {
                                override fun onDownloadStart() {}
                                override fun onDownloadProgress(progress: Int, downloadedBytes: Long, totalBytes: Long?) {}
                                override fun onDownloadSuccess() {}
                                override fun onError(error: Throwable) {
                                    // Lidar com erros de carregamento/renderização
                                    error.printStackTrace()
                                    viewModel.setErrorMessage("Erro ao carregar PDF: ${error.localizedMessage}") // Atualizar estado de erro no ViewModel
                                    Toast.makeText(context, "Erro ao carregar PDF", Toast.LENGTH_SHORT).show()
                                }

                                override fun onPageChanged(currentPage: Int, totalPage: Int) {
                                    // Atualizar o estado do Compose via ViewModel
                                    viewModel.updateCurrentPage(currentPage)
                                    viewModel.updateTotalPages(totalPage)
                                }
                            }
                        }
                    },
                    update = { view ->
                        // Navegar para a página quando currentPage muda no Compose
                        view.jumpTo(currentPage)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                // Controles de navegação
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.goToPreviousPage() }, // Chamar função no ViewModel
                        enabled = currentPage > 0
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Página anterior")
                    }
                    Text("Página ${currentPage + 1} de $totalPages")
                    IconButton(
                        onClick = { viewModel.goToNextPage() }, // Chamar função no ViewModel para avançar página
                        enabled = currentPage < totalPages - 1
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Próxima página")
                    }
                }

                // Exibir mensagem de erro se houver
                errorMessage?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error)
                }
            }
            "mp3" -> {
                // Mantendo o placeholder atual para o visualizador de música, aguardando clarificação.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Visualizador de música em desenvolvimento")
                }
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tipo de arquivo não suportado")
                }
            }
        }
    }
} 