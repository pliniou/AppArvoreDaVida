package com.example.apparvoredavida.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apparvoredavida.util.UiState

/**
 * Componente que exibe o estado da UI.
 *
 * @param state Estado atual da UI
 * @param content Conteúdo a ser exibido quando o estado for de sucesso
 * @param modifier Modificador do layout
 */
@Composable
fun <T> UiStateHandler(
    state: UiState<T>,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success -> {
                content(state.data)
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    state.retry?.let { retry ->
                        Button(
                            onClick = retry,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
        }
    }
} 