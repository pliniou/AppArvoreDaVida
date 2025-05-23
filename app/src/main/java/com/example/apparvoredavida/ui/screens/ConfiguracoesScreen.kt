package com.example.apparvoredavida.ui.screens

import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.item
import java.io.File
import com.example.apparvoredavida.viewmodel.PreferenciasViewModel
import com.example.apparvoredavida.model.TemaApp
import com.example.apparvoredavida.util.TamanhoFonte
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.Constants
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(navController: NavController) {
    val context = LocalContext.current
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
            // Seção de Tema
            item {
                Text("Tema", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TemaApp.values().forEach { tema ->
                        FilterChip(
                            selected = preferencias.tema == tema,
                            onClick = { viewModel.atualizarTema(tema) },
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

            // Seção de Fonte
            item {
                Text("Fonte", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(fontesDisponiveis) { fonteNome ->
                        FilterChip(
                            selected = preferencias.fonte == fonteNome,
                            onClick = { viewModel.atualizarFonte(fonteNome) },
                            label = { Text(fonteNome) }
                        )
                    }
                }
            }

            // Seção de Tamanho da Fonte
            item {
                Text("Tamanho da Fonte", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Slider(
                    value = preferencias.tamanhoFonte.ordinal.toFloat(),
                    onValueChange = { newValue ->
                        val novoTamanho = TamanhoFonte.values().getOrElse(newValue.toInt()) { TamanhoFonte.MEDIO }
                        viewModel.atualizarTamanhoFonte(novoTamanho)
                    },
                    valueRange = 0f..(TamanhoFonte.values().size - 1).toFloat(),
                    steps = TamanhoFonte.values().size - 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Tamanho atual: ${preferencias.tamanhoFonte}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// Função utilitária para carregar FontFamily dinamicamente de assets/fonts/
// ... existing code ... 