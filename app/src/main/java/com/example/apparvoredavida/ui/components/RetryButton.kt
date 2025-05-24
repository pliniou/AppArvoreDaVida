package com.example.apparvoredavida.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Componente que exibe um botão de retry.
 *
 * @param onClick Função a ser chamada quando o botão for clicado
 * @param modifier Modificador do layout
 */
@Composable
fun RetryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(16.dp)
    ) {
        Text("Tentar novamente")
    }
} 