package com.example.apparvoredavida.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.apparvoredavida.R
import com.example.apparvoredavida.model.Album

/**
 * Componente que exibe o cabeçalho de um álbum com sua capa e informações básicas.
 * @param album Álbum a ser exibido
 * @param modifier Modificador para personalizar o layout
 */
@Composable
fun AlbumHeader(album: Album, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = album.coverPath ?: R.drawable.ic_default_album_art,
            contentDescription = "Capa do álbum ${album.title}",
            modifier = Modifier
                .size(200.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = album.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        album.artist?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Componente que exibe um card de álbum com suas informações e botão de favorito.
 * @param album Álbum a ser exibido
 * @param isFavorite Indica se o álbum está nos favoritos
 * @param onClick Callback para quando o card é clicado
 * @param onToggleFavorite Callback para quando o botão de favorito é clicado
 */
@Composable
fun AlbumCard(
    album: Album,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos"
                    )
                }
            }
            Text(
                text = album.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${album.musics?.size ?: 0} músicas",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 