package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apparvoredavida.model.Album
import com.example.apparvoredavida.model.Music
import com.example.apparvoredavida.viewmodel.MusicaViewModel
import com.example.apparvoredavida.viewmodel.FavoritesViewModel
import com.example.apparvoredavida.ui.components.MusicListItem
import com.example.apparvoredavida.ui.components.AlbumHeader
import com.example.apparvoredavida.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    navController: NavController,
    albumId: String,
    musicaViewModel: MusicaViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val albumComMusicasState by musicaViewModel.getAlbumById(albumId).collectAsState(initial = null)
    val isLoading = albumComMusicasState == null && albumId.isNotEmpty()
    val albumData = albumComMusicasState?.first
    val musicList = albumComMusicasState?.second ?: emptyList()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = albumData?.title ?: "Detalhes do Álbum",
            onBackClick = { navController.navigateUp() }
        )

        when {
            isLoading -> LoadingContent()
            albumData == null -> ErrorContent()
            else -> AlbumContent(
                album = albumData,
                musicList = musicList,
                favoritesViewModel = favoritesViewModel,
                onMusicClick = { selectedMusic ->
                    musicaViewModel.selecionarMusica(selectedMusic)
                    navController.popBackStack()
                }
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
private fun ErrorContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Álbum não encontrado.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun AlbumContent(
    album: Album,
    musicList: List<Music>,
    favoritesViewModel: FavoritesViewModel,
    onMusicClick: (Music) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AlbumHeader(album = album)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(musicList) { musica ->
            val isFavorite by favoritesViewModel.isMusicFavoriteFlow(musica.id)
                .collectAsState(initial = false)
            
            MusicListItem(
                music = musica,
                isFavorite = isFavorite,
                onMusicClick = onMusicClick,
                onToggleFavorite = { favoritesViewModel.toggleFavoriteMusic(it) }
            )
            Divider()
        }
    }
} 