package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.viewmodel.MusicaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReprodutorScreen(
    navController: NavController,
    musicId: String
) {
    val viewModel: MusicaViewModel = hiltViewModel()
    val music by viewModel.musicaSelecionada.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle(initialValue = false)
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle(initialValue = 0L)
    val duration by viewModel.duration.collectAsStateWithLifecycle(initialValue = 0L)

    LaunchedEffect(musicId) {
        viewModel.loadMusic(musicId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = music?.title ?: "Reprodutor",
            onBackClick = { navController.navigateUp() }
        )

        if (music == null) {
            LoadingContent()
        } else {
            MusicPlayerContent(
                music = music!!,
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                onPlayPause = { viewModel.togglePlayPause() },
                onSeek = { position -> viewModel.seekTo(position) }
            )
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
private fun MusicPlayerContent(
    music: com.example.apparvoredavida.model.Music,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MusicInfo(music = music)
        Spacer(modifier = Modifier.height(32.dp))
        ProgressBar(
            currentPosition = currentPosition,
            duration = duration,
            onSeek = onSeek
        )
        PlaybackControls(
            isPlaying = isPlaying,
            onPlayPause = onPlayPause
        )
    }
}

@Composable
private fun MusicInfo(music: com.example.apparvoredavida.model.Music) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = music.title,
            style = MaterialTheme.typography.headlineMedium
        )
        music.artist?.let { artist ->
            Text(
                text = artist,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ProgressBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit
) {
    Column {
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatDuration(currentPosition))
            Text(formatDuration(duration))
        }
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
} 