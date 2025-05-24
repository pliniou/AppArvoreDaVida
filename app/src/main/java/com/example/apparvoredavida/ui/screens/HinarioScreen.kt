package com.example.apparvoredavida.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.apparvoredavida.model.HymnInfo
import com.example.apparvoredavida.viewmodel.HinarioViewModel
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.ui.navigation.Screen
import com.example.apparvoredavida.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HinarioScreen(navController: NavController) {
    val viewModel: HinarioViewModel = hiltViewModel()
    val hymns by viewModel.hymns.collectAsStateWithLifecycle(initialValue = emptyList())
    val favoriteHymns by viewModel.favoriteHymns.collectAsStateWithLifecycle(initialValue = emptySet())
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Hinário",
            onBackClick = { navController.navigateUp() }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )

        when {
            hymns.isEmpty() -> LoadingContent()
            hymns.filter { hymn -> hymn.title.contains(searchQuery, ignoreCase = true) }.isEmpty() -> {
                EmptySearchResult()
            }
            else -> HymnsList(
                hymns = hymns.filter { hymn -> hymn.title.contains(searchQuery, ignoreCase = true) },
                favoriteHymns = favoriteHymns,
                onHymnClick = { hymn -> handleHymnClick(hymn, navController) },
                onToggleFavorite = { hymn -> handleToggleFavorite(hymn, viewModel) }
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.padding(16.dp),
        placeholder = { Text("Buscar hinos...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        singleLine = true
    )
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
private fun EmptySearchResult() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nenhum hino encontrado",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HymnsList(
    hymns: List<HymnInfo>,
    favoriteHymns: Set<String>,
    onHymnClick: (HymnInfo) -> Unit,
    onToggleFavorite: (HymnInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hymns, key = { it.id }) { hymn ->
            HymnItem(
                hymn = hymn,
                isFavorite = favoriteHymns.contains(hymn.id),
                onClick = { onHymnClick(hymn) },
                onToggleFavorite = { onToggleFavorite(hymn) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HymnItem(
    hymn: HymnInfo,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ListItem(
            headlineContent = { Text("${hymn.number}. ${hymn.title}") },
            supportingContent = {
                Text(
                    text = hymn.lyrics.take(100) + if (hymn.lyrics.length > 100) "..." else "",
                    maxLines = 2
                )
            },
            trailingContent = {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

private fun handleHymnClick(hymn: HymnInfo, navController: NavController) {
    navController.navigate(Screen.Viewer.createRoute(hymn.id, "pdf"))
}

private fun handleToggleFavorite(hymn: HymnInfo, viewModel: HinarioViewModel) {
    viewModel.toggleFavoriteHymn(hymn.id)
} 