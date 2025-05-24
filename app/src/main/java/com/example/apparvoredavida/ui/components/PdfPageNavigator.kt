package com.example.apparvoredavida.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PdfPageNavigator(
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousPage,
            enabled = currentPage > 0
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Página anterior",
                tint = if (currentPage > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${currentPage + 1} / $totalPages",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onNextPage,
            enabled = currentPage < totalPages - 1
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Próxima página",
                tint = if (currentPage < totalPages - 1) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
} 