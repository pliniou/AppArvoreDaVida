package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.apparvoredavida.model.FavoriteDisplayItem
import com.example.apparvoredavida.viewmodel.FavoritesViewModel
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: FavoritesViewModel = hiltViewModel()
    
    val favoriteItems by viewModel.allFavoriteItems.collectAsStateWithLifecycle()
    
    val onItemClick: (FavoriteDisplayItem) -> Unit = { item ->
        when (item) {
            is FavoriteDisplayItem.MusicItem -> { navController.navigate("${Constants.ROUTE_PLAYER}/${item.id}") }
            is FavoriteDisplayItem.VerseItem -> {
                // Construir a rota manualmente
                val traducaoAbrev = item.verseObject.translation?.abbreviation ?: "NVI" // Assumindo propriedade 'translation' com 'abbreviation', default para NVI
                val livroAbrev = item.verseObject.book.abbreviation // Assumindo propriedade 'book' com 'abbreviation'
                val capituloNumero = item.verseObject.chapter
                val versiculoNumero = item.verseObject.verseNumber
                navController.navigate("biblia_screen/${traducaoAbrev}/${livroAbrev}/${capituloNumero}/${versiculoNumero}")
            }
            is FavoriteDisplayItem.HymnItem -> { navController.navigate("${Constants.ROUTE_VIEWER}/${item.id}/pdf") }
            is FavoriteDisplayItem.ScoreItem -> { navController.navigate("${Constants.ROUTE_VIEWER}/${item.id}/pdf") }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Meus Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (favoriteItems.isEmpty()) {
            Text(
                text = "Nenhum item favoritado ainda.",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteItems, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = {
                            Column {
                                Text("Tipo: ${item::class.simpleName}", style = MaterialTheme.typography.bodySmall)
                                when (item) {
                                    is FavoriteDisplayItem.MusicItem -> {
                                        item.musicObject.artist?.let { artist ->
                                            Text("Artista: $artist", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    is FavoriteDisplayItem.VerseItem -> {
                                        Text("Referência: ${item.reference}", style = MaterialTheme.typography.bodySmall)
                                        Text("${item.verseObject.verseNumber}. ${item.verseObject.text}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    is FavoriteDisplayItem.HymnItem -> {
                                        // Add any specific hymn info if available in HymnInfo
                                    }
                                    is FavoriteDisplayItem.ScoreItem -> {
                                         // Add any specific score info if available in ScoreInfo
                                    }
                                }
                            }
                        },
                        trailingContent = {
                             IconButton(onClick = {
                                when (item) {
                                    is FavoriteDisplayItem.MusicItem -> viewModel.toggleFavoriteMusic(item.musicObject)
                                    is FavoriteDisplayItem.VerseItem -> viewModel.toggleFavoriteVerse(item.id)
                                    is FavoriteDisplayItem.HymnItem -> viewModel.toggleFavoriteHymn(item.id)
                                    is FavoriteDisplayItem.ScoreItem -> viewModel.toggleFavoriteScore(item.id)
                                }
                            }) {
                                Icon(Icons.Filled.Favorite, contentDescription = "Remover dos favoritos")
                            }
                        },
                        modifier = Modifier.clickable { onItemClick(item) }
                    )
                }
            }
        }
    }
} 