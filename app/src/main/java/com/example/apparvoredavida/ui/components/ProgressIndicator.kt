package com.example.apparvoredavida.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Componente que exibe um indicador de progresso circular.
 *
 * @param modifier Modificador do layout
 */
@Composable
fun CircularProgress(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Componente que exibe um indicador de progresso linear.
 *
 * @param progress Progresso atual (0.0 a 1.0)
 * @param modifier Modificador do layout
 */
@Composable
fun LinearProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Componente que exibe um indicador de progresso com texto.
 *
 * @param progress Progresso atual (0.0 a 1.0)
 * @param text Texto a ser exibido
 * @param modifier Modificador do layout
 */
@Composable
fun ProgressWithText(
    progress: Float,
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
} 