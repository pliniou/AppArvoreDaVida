package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apparvoredavida.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Árvore da Vida",
            onBackClick = null
        )

        HomeContent(
            onBibliaClick = { navController.navigate("biblia_screen") },
            onMusicasClick = { navController.navigate("musicas_screen") },
            onHinarioClick = { navController.navigate("hinario_screen") },
            onPartiturasClick = { navController.navigate("partituras_screen") },
            onFavoritosClick = { navController.navigate("favoritos_screen") },
            onConfiguracoesClick = { navController.navigate("configuracoes_screen") }
        )
    }
}

@Composable
private fun HomeContent(
    onBibliaClick: () -> Unit,
    onMusicasClick: () -> Unit,
    onHinarioClick: () -> Unit,
    onPartiturasClick: () -> Unit,
    onFavoritosClick: () -> Unit,
    onConfiguracoesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeButton(
            text = "Bíblia",
            icon = Icons.Default.MenuBook,
            onClick = onBibliaClick
        )

        HomeButton(
            text = "Músicas",
            icon = Icons.Default.MusicNote,
            onClick = onMusicasClick
        )

        HomeButton(
            text = "Hinário",
            icon = Icons.Default.LibraryMusic,
            onClick = onHinarioClick
        )

        HomeButton(
            text = "Partituras",
            icon = Icons.Default.Score,
            onClick = onPartiturasClick
        )

        HomeButton(
            text = "Favoritos",
            icon = Icons.Default.Favorite,
            onClick = onFavoritosClick
        )

        HomeButton(
            text = "Configurações",
            icon = Icons.Default.Settings,
            onClick = onConfiguracoesClick
        )
    }
}

@Composable
private fun HomeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
} 