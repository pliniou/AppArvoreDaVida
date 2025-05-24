package com.example.apparvoredavida.ui.components

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun AppPdfViewer(
    modifier: Modifier = Modifier,
    file: File,
    onClose: () -> Unit,
    initialPage: Int = 0,
    onPageChanged: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pageCount by remember { mutableStateOf(0) }
    var currentPage by remember { mutableStateOf(initialPage) }
    var zoomLevel by remember { mutableStateOf(1f) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(file) {
        try {
            withContext(Dispatchers.IO) {
                val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfRenderer = PdfRenderer(fileDescriptor)
                pageCount = pdfRenderer?.pageCount ?: 0
                isLoading = false
            }
        } catch (e: Exception) {
            error = "Erro ao carregar PDF: ${e.message}"
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            pdfRenderer?.close()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            error != null -> {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(pageCount) { pageIndex ->
                        PdfPage(
                            pdfRenderer = pdfRenderer,
                            pageIndex = pageIndex,
                            zoomLevel = zoomLevel,
                            onZoomChanged = { zoomLevel = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfPage(
    pdfRenderer: PdfRenderer?,
    pageIndex: Int,
    zoomLevel: Float,
    onZoomChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(pdfRenderer, pageIndex) {
        pdfRenderer?.let { renderer ->
            withContext(Dispatchers.IO) {
                renderer.openPage(pageIndex).use { page ->
                    val newBitmap = android.graphics.Bitmap.createBitmap(
                        page.width * 2,
                        page.height * 2,
                        android.graphics.Bitmap.Config.ARGB_8888
                    )
                    page.render(newBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmap = newBitmap
                }
            }
        }
    }

    bitmap?.let { bmp ->
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "Página ${pageIndex + 1}",
            modifier = modifier
                .graphicsLayer(
                    scaleX = zoomLevel,
                    scaleY = zoomLevel
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        onZoomChanged((zoomLevel * zoom).coerceIn(0.5f, 3f))
                    }
                },
            contentScale = ContentScale.Fit
        )
    }
} 