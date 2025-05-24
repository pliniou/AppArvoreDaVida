package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.apparvoredavida.viewmodel.PreferenciasViewModel
import com.example.apparvoredavida.model.TemaApp
import com.example.apparvoredavida.util.TamanhoFonte
import com.example.apparvoredavida.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(navController: NavController) {
    val viewModel: PreferenciasViewModel = hiltViewModel()
    val preferencias by viewModel.preferencias.collectAsStateWithLifecycle()
    val fontesDisponiveis by viewModel.fontesDisponiveis.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Configurações",
            onBackClick = { navController.navigateUp() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TemaSection(
                    temaAtual = preferencias.tema,
                    onTemaSelected = { viewModel.atualizarTema(it) }
                )
            }

            item {
                FonteSection(
                    fontesDisponiveis = fontesDisponiveis,
                    fonteAtual = preferencias.fonte,
                    onFonteSelected = { viewModel.atualizarFonte(it) }
                )
            }

            item {
                TamanhoFonteSection(
                    tamanhoAtual = preferencias.tamanhoFonte,
                    onTamanhoChanged = { viewModel.atualizarTamanhoFonte(it) }
                )
            }
        }
    }
}

@Composable
private fun TemaSection(
    temaAtual: TemaApp,
    onTemaSelected: (TemaApp) -> Unit
) {
    Column {
        Text("Tema", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TemaApp.values().forEach { tema ->
                FilterChip(
                    selected = temaAtual == tema,
                    onClick = { onTemaSelected(tema) },
                    label = {
                        Text(
                            when (tema) {
                                TemaApp.CLARO -> "Claro"
                                TemaApp.ESCURO -> "Escuro"
                                TemaApp.SISTEMA -> "Sistema"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun FonteSection(
    fontesDisponiveis: List<String>,
    fonteAtual: String,
    onFonteSelected: (String) -> Unit
) {
    Column {
        Text("Fonte", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fontesDisponiveis) { fonteNome ->
                FilterChip(
                    selected = fonteAtual == fonteNome,
                    onClick = { onFonteSelected(fonteNome) },
                    label = { Text(fonteNome) }
                )
            }
        }
    }
}

@Composable
private fun TamanhoFonteSection(
    tamanhoAtual: TamanhoFonte,
    onTamanhoChanged: (TamanhoFonte) -> Unit
) {
    Column {
        Text("Tamanho da Fonte", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Slider(
            value = tamanhoAtual.ordinal.toFloat(),
            onValueChange = { newValue ->
                val novoTamanho = TamanhoFonte.values().getOrElse(newValue.toInt()) { TamanhoFonte.MEDIO }
                onTamanhoChanged(novoTamanho)
            },
            valueRange = 0f..(TamanhoFonte.values().size - 1).toFloat(),
            steps = TamanhoFonte.values().size - 2,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Tamanho atual: ${tamanhoAtual}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 