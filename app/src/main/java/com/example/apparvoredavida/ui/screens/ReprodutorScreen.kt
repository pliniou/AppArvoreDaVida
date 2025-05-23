@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.example.apparvoredavida.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.apparvoredavida.viewmodel.MusicaViewModel
import com.example.apparvoredavida.model.Music
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.media3.datasource.AssetDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.mutableLongStateOf
import java.io.File
import com.example.apparvoredavida.ui.components.AppTopBar
import java.util.concurrent.TimeUnit

fun formatTime(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun ReprodutorScreen(
    navController: NavController,
    musicaPath: String? = null,
    viewModel: MusicaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Obter a música selecionada diretamente do ViewModel
    val musica by viewModel.musicaSelecionada.collectAsStateWithLifecycle()

    // --- Observar estados do ViewModel ---
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    // ------------------------------------

    // Carregar música no player quando a música selecionada mudar no ViewModel
    LaunchedEffect(musica) {
        musica?.let {
            viewModel.playMusic(it)
        }
    }

    // UI
    musica?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.background)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            // Capa
            if (it.coverPath != null) {
                val coverFile = remember(it.coverPath) { File(it.coverPath) }
                if (coverFile.exists()) {
                    val bitmap = remember(coverFile) { BitmapFactory.decodeFile(coverFile.absolutePath) }
                    bitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Capa do álbum",
                            modifier = Modifier.size(220.dp).clip(CircleShape)
                        )
                    }
                } else {
                    Icon(Icons.Default.Album, contentDescription = null, modifier = Modifier.size(220.dp))
                }
            } else {
                Icon(Icons.Default.Album, contentDescription = null, modifier = Modifier.size(220.dp))
            }
            Spacer(Modifier.height(32.dp))
            // Nome e autor
            Text(it.title, style = MaterialTheme.typography.headlineSmall)
            it.artist?.let { artista ->
                Text(artista, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(32.dp))
            // Barra de progresso animada (usando currentPosition e duration do ViewModel)
            PlayerProgressBar(currentPosition, duration)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentPosition))
                Text(formatTime(duration))
            }
            Spacer(Modifier.height(16.dp))
            // Controles (chamando funções do ViewModel)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Anterior */ }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior", modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = { if (isPlaying) viewModel.pauseMusic() else musica?.let { viewModel.playMusic(it) } }) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = { /* Próxima */ }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Próxima", modifier = Modifier.size(48.dp))
                }
            }
        }
    } ?: run {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            AppTopBar(title = "Reproduzindo", onBackClick = { navController.navigateUp() })
            Text("Carregando música...")
        }
    }
}

@Composable
private fun PlayerProgressBar(position: Long, duration: Long) {
    val progress = if (duration > 0) position.toFloat() / duration else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.secondaryContainer
        )
        // Simulação de onda pode ser implementada com Canvas customizado se desejar
    }
} 