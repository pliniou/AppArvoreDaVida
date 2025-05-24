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
import com.example.apparvoredavida.model.ScoreInfo
import com.example.apparvoredavida.viewmodel.PartiturasViewModel
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.ui.navigation.Screen
import com.example.apparvoredavida.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartiturasScreen(navController: NavController) {
    val viewModel: PartiturasViewModel = hiltViewModel()
    val scores by viewModel.scores.collectAsStateWithLifecycle(initialValue = emptyList())
    val favoriteScores by viewModel.favoriteScores.collectAsStateWithLifecycle(initialValue = emptySet())
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Partituras",
            onBackClick = { navController.navigateUp() }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )

        when {
            scores.isEmpty() -> LoadingContent()
            scores.filter { score -> score.title.contains(searchQuery, ignoreCase = true) }.isEmpty() -> {
                EmptySearchResult()
            }
            else -> ScoresList(
                scores = scores.filter { score -> score.title.contains(searchQuery, ignoreCase = true) },
                favoriteScores = favoriteScores,
                onScoreClick = { score -> handleScoreClick(score, navController) },
                onToggleFavorite = { score -> handleToggleFavorite(score, viewModel) }
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
        placeholder = { Text("Buscar partituras...") },
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
                text = "Nenhuma partitura encontrada",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScoresList(
    scores: List<ScoreInfo>,
    favoriteScores: Set<String>,
    onScoreClick: (ScoreInfo) -> Unit,
    onToggleFavorite: (ScoreInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(scores, key = { it.id }) { score ->
            ScoreItem(
                score = score,
                isFavorite = favoriteScores.contains(score.id),
                onClick = { onScoreClick(score) },
                onToggleFavorite = { onToggleFavorite(score) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScoreItem(
    score: ScoreInfo,
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
            headlineContent = { Text(score.title) },
            supportingContent = {
                Text(
                    text = score.description.take(100) + if (score.description.length > 100) "..." else "",
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

private fun handleScoreClick(score: ScoreInfo, navController: NavController) {
    navController.navigate(Screen.Viewer.createRoute(score.id, "pdf"))
}

private fun handleToggleFavorite(score: ScoreInfo, viewModel: PartiturasViewModel) {
    viewModel.toggleFavoriteScore(score.id)
} 