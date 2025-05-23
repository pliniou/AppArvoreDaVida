package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.example.apparvoredavida.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    navController: NavController,
    albumId: String, // Recebe o ID do álbum da rota
    musicaViewModel: MusicaViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    // 1. Carregar dados do álbum e suas músicas baseado no albumId
    //    Idealmente, o MusicaViewModel teria uma função para isso.
    //    Ex: musicaViewModel.loadAlbumDetails(albumId)
    //    E um StateFlow para o álbum detalhado e suas músicas.

    // Exemplo de como obter o álbum (pode variar dependendo da sua lógica no ViewModel)
    val albumComMusicasState by musicaViewModel.getAlbumById(albumId).collectAsState(initial = null)

    // Obter dados do álbum e músicas, e definir estado de carregamento/erro
    val isLoading = albumComMusicasState == null && albumId.isNotEmpty()
    val albumData = albumComMusicasState?.first
    val musicList = albumComMusicasState?.second ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(albumData?.title ?: "Detalhes do Álbum") }, // Usar albumData para o título
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Conteúdo principal lidando com os estados de carregamento, erro e sucesso
        if (isLoading) {
            // Estado de Carregamento
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (albumData == null) { // Considera erro ou não encontrado se não está carregando e albumData é null
            // Estado de Erro/Álbum Não Encontrado
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Álbum não encontrado.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            // Estado de Sucesso (Álbum encontrado)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp) // Manter padding horizontal se necessário
            ) {
                // Adicionar o AlbumHeader como primeiro item
                item {
                    AlbumHeader(album = albumData) // Passar albumData
                    Spacer(modifier = Modifier.height(16.dp)) // Espaço entre header e lista
                }

                // Manter a lista de músicas
                items(musicList) { musica -> // Usar musicList
                    val isFavorite by favoritesViewModel.isMusicFavoriteFlow(musica.id).collectAsState(initial = false)
                    MusicListItem(
                        music = musica,
                        isFavorite = isFavorite,
                        onMusicClick = { selectedMusic ->
                            musicaViewModel.selecionarMusica(selectedMusic)
                            // Navega de volta para MusicasScreen, limpando AlbumDetailScreen da pilha.
                            // MusicasScreen observará musicaSelecionada e mostrará o player.
                            navController.popBackStack(Screen.Music.route, inclusive = false)
                        },
                        onToggleFavorite = { toggledMusic ->
                            favoritesViewModel.toggleFavoriteMusic(toggledMusic)
                        }
                    )
                    Divider() // Manter Divider entre itens
                }
            }
        }
    }
} 