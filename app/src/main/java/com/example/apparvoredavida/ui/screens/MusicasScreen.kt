package com.example.apparvoredavida.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.model.Album
import com.example.apparvoredavida.model.Music
import com.example.apparvoredavida.viewmodel.FavoritesViewModel
import com.example.apparvoredavida.ui.components.LoadingOverlay
import com.example.apparvoredavida.viewmodel.MusicaViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apparvoredavida.viewmodel.VisualizacaoMusica
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.apparvoredavida.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicasScreen(navController: NavController = rememberNavController()) {
    val context = LocalContext.current
    val musicaViewModel: MusicaViewModel = hiltViewModel()
    val favoritosViewModel: FavoritesViewModel = hiltViewModel()
    
    val albuns by musicaViewModel.albuns.collectAsStateWithLifecycle()
    val isLoading by musicaViewModel.isLoading.collectAsStateWithLifecycle()
    val albumExpandido by musicaViewModel.albumExpandido.collectAsStateWithLifecycle()
    val visualizacao by musicaViewModel.visualizacao.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }

    LoadingOverlay(isLoading = isLoading) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "Músicas",
                onBackClick = { navController.navigateUp() },
                actions = {
                    SearchBar(
                        searchText = searchText,
                        onSearchTextChange = { searchText = it }
                    )
                    VisualizacaoToggle(
                        visualizacao = visualizacao,
                        onToggle = { musicaViewModel.toggleVisualizacao() }
                    )
                }
            )

            val filteredAlbuns = remember(albuns, searchText) {
                if (searchText.isBlank()) albuns else albuns.filter { 
                    it.title.contains(searchText, ignoreCase = true) 
                }
            }

            when (visualizacao) {
                VisualizacaoMusica.LISTA -> ListaAlbuns(
                    albuns = filteredAlbuns,
                    albumExpandido = albumExpandido,
                    onExpandClick = { musicaViewModel.expandirAlbum(it) },
                    onMusicClick = { musica ->
                        musicaViewModel.selecionarMusica(musica)
                        navController.navigate("${Constants.ROUTE_PLAYER}/${musica.id}")
                    },
                    onFavoriteClick = { favoritosViewModel.toggleFavoriteMusic(it) },
                    favoritosViewModel = favoritosViewModel,
                    musicaViewModel = musicaViewModel
                )
                VisualizacaoMusica.GRID -> GridAlbuns(
                    albuns = filteredAlbuns,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        label = { Text("Buscar") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        modifier = Modifier
            .padding(end = 8.dp)
            .width(200.dp)
    )
}

@Composable
private fun VisualizacaoToggle(
    visualizacao: VisualizacaoMusica,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (visualizacao == VisualizacaoMusica.LISTA) Icons.Default.GridView else Icons.AutoMirrored.Filled.List,
            contentDescription = if (visualizacao == VisualizacaoMusica.LISTA) "Mudar para visualização em grade" else "Mudar para visualização em lista"
        )
    }
}

@Composable
private fun ListaAlbuns(
    albuns: List<Album>,
    albumExpandido: String?,
    onExpandClick: (String) -> Unit,
    onMusicClick: (Music) -> Unit,
    onFavoriteClick: (Music) -> Unit,
    favoritosViewModel: FavoritesViewModel,
    musicaViewModel: MusicaViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albuns) { album ->
            val musicasFavoritas = album.musics.associate { musica ->
                musica.id to favoritosViewModel.isMusicFavoriteFlow(musica.id)
                    .collectAsStateWithLifecycle(initialValue = false)
                    .value
            }
            
            AlbumCard(
                album = album,
                isExpanded = albumExpandido == album.id,
                onExpandClick = { onExpandClick(album.id) },
                onMusicClick = onMusicClick,
                onFavoriteClick = onFavoriteClick,
                musicasFavoritas = musicasFavoritas,
                musics = if (albumExpandido == album.id) {
                    musicaViewModel.getAlbumById(album.id).collectAsStateWithLifecycle().value.second
                } else {
                    emptyList()
                }
            )
        }
    }
}

@Composable
private fun GridAlbuns(
    albuns: List<Album>,
    navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(albuns) { album ->
            AlbumGridItem(
                album = album,
                navController = navController
            )
        }
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onMusicClick: (Music) -> Unit,
    onFavoriteClick: (Music) -> Unit,
    musicasFavoritas: Map<String, Boolean>,
    musics: List<Music>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onExpandClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onExpandClick) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Recolher" else "Expandir"
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    musics.forEach { music ->
                        MusicListItem(
                            music = music,
                            onMusicClick = onMusicClick,
                            onFavoriteClick = onFavoriteClick,
                            isFavorite = musicasFavoritas[music.id] ?: false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicListItem(
    music: Music,
    onMusicClick: (Music) -> Unit,
    onFavoriteClick: (Music) -> Unit,
    isFavorite: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMusicClick(music) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = music.title, style = MaterialTheme.typography.bodyLarge)
            music.artist?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
        }
        IconButton(onClick = { onFavoriteClick(music) }) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }
    }
}

@Composable
private fun AlbumCover(album: Album) {
    if (album.coverPath != null) {
        val bitmap = remember(album.coverPath) { BitmapFactory.decodeFile(album.coverPath) }
        bitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "Capa do álbum", modifier = Modifier.size(64.dp))
        }
    } else {
        Icon(Icons.Default.Album, contentDescription = "Capa padrão", modifier = Modifier.size(64.dp))
    }
}

@Composable
private fun AlbumGridItem(
    album: Album,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val albumInfo = album

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                navController.navigate(Screen.AlbumDetail.createRoute(albumInfo.id))
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AlbumCover(album = albumInfo)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = albumInfo.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            albumInfo.artist?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
} 