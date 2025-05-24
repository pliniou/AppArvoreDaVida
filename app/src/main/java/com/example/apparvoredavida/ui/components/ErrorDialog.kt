package com.example.apparvoredavida.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Componente que exibe um diálogo de erro.
 *
 * @param title Título do diálogo
 * @param message Mensagem de erro
 * @param onDismiss Função a ser chamada quando fechar o diálogo
 * @param onRetry Função opcional para tentar novamente
 */
@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = if (onRetry != null) {
            {
                Button(onClick = onRetry) {
                    Text("Tentar novamente")
                }
            }
        } else null
    )
} 