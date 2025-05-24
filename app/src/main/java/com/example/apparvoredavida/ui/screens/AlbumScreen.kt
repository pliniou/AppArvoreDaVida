package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.apparvoredavida.model.Music
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.viewmodel.MusicaViewModel
import com.example.apparvoredavida.viewmodel.FavoritesViewModel
import com.example.apparvoredavida.util.Constants
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    navController: NavController,
    albumId: String,
    musicaViewModel: MusicaViewModel = hiltViewModel(),
    favoritosViewModel: FavoritesViewModel = hiltViewModel()
) {
    val albumComMusicasState by musicaViewModel.getAlbumById(albumId).collectAsStateWithLifecycle()
    val isLoading = albumComMusicasState == null && albumId.isNotEmpty()
    val albumData = albumComMusicasState.first
    val musicList = albumComMusicasState.second

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = albumData?.title ?: "Álbum",
            onBackClick = { navController.navigateUp() }
        )

        when {
            isLoading -> LoadingContent()
            albumData == null -> ErrorContent()
            else -> MusicListContent(
                musicList = musicList,
                favoritosViewModel = favoritosViewModel,
                musicaViewModel = musicaViewModel,
                navController = navController
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
private fun MusicListContent(
    musicList: List<Music>,
    favoritosViewModel: FavoritesViewModel,
    musicaViewModel: MusicaViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(musicList) { musica ->
            val isFavorite by favoritosViewModel.isMusicFavoriteFlow(musica.id)
                .collectAsState(initial = false)
            
            MusicItem(
                musica = musica,
                isFavorite = isFavorite,
                onPlayClick = {
                    musicaViewModel.selecionarMusica(musica)
                    navController.navigate("${Constants.ROUTE_PLAYER}/${musica.id}")
                },
                onFavoriteClick = { favoritosViewModel.toggleFavoriteMusic(it) }
            )
        }
    }
}

@Composable
private fun MusicItem(
    musica: Music,
    isFavorite: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteClick: (Music) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onPlayClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = musica.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    musica.artist?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            IconButton(onClick = { onFavoriteClick(musica) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}