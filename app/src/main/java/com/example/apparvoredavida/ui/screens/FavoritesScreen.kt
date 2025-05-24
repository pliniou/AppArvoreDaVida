package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.apparvoredavida.model.FavoriteDisplayItem
import com.example.apparvoredavida.viewmodel.FavoritesViewModel
import com.example.apparvoredavida.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val favoriteItems by viewModel.allFavoriteItems.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Meus Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (favoriteItems.isEmpty()) {
            EmptyFavoritesContent()
        } else {
            FavoritesList(
                items = favoriteItems,
                onItemClick = { item -> handleItemClick(item, navController) },
                onToggleFavorite = { item -> handleToggleFavorite(item, viewModel) }
            )
        }
    }
}

@Composable
private fun EmptyFavoritesContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nenhum item favoritado ainda.",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun FavoritesList(
    items: List<FavoriteDisplayItem>,
    onItemClick: (FavoriteDisplayItem) -> Unit,
    onToggleFavorite: (FavoriteDisplayItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            FavoriteItem(
                item = item,
                onClick = { onItemClick(item) },
                onToggleFavorite = { onToggleFavorite(item) }
            )
        }
    }
}

@Composable
private fun FavoriteItem(
    item: FavoriteDisplayItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
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
                        Text(
                            "${item.verseObject.versiculoNumero}. ${item.verseObject.texto}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    is FavoriteDisplayItem.HymnItem -> {
                        // Informações específicas de hino, se disponíveis
                    }
                    is FavoriteDisplayItem.ScoreItem -> {
                        // Informações específicas de partitura, se disponíveis
                    }
                }
            }
        },
        trailingContent = {
            IconButton(onClick = onToggleFavorite) {
                Icon(Icons.Filled.Favorite, contentDescription = "Remover dos favoritos")
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

private fun handleItemClick(item: FavoriteDisplayItem, navController: NavController) {
    when (item) {
        is FavoriteDisplayItem.MusicItem -> {
            navController.navigate("${Constants.ROUTE_PLAYER}/${item.id}")
        }
        is FavoriteDisplayItem.VerseItem -> {
            val verseDetails = item.verseObject
            val parts = verseDetails.id.split("_")
            if (parts.size == 5) {
                val (traducaoAbrev, livroAbrev, capitulo, versiculo, _) = parts
                navController.navigate(
                    "biblia_screen/$traducaoAbrev/$livroAbrev/$capitulo/$versiculo"
                )
            }
        }
        is FavoriteDisplayItem.HymnItem -> {
            navController.navigate("${Constants.ROUTE_VIEWER}/${item.id}/pdf")
        }
        is FavoriteDisplayItem.ScoreItem -> {
            navController.navigate("${Constants.ROUTE_VIEWER}/${item.id}/pdf")
        }
    }
}

private fun handleToggleFavorite(item: FavoriteDisplayItem, viewModel: FavoritesViewModel) {
    when (item) {
        is FavoriteDisplayItem.MusicItem -> viewModel.toggleFavoriteMusic(item.musicObject)
        is FavoriteDisplayItem.VerseItem -> viewModel.toggleFavoriteVerse(item.id)
        is FavoriteDisplayItem.HymnItem -> viewModel.toggleFavoriteHymn(item.id)
        is FavoriteDisplayItem.ScoreItem -> viewModel.toggleFavoriteScore(item.id)
    }
} 