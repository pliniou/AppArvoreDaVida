package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.ui.components.AppCard
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.viewmodel.BibliaViewModel
import com.example.apparvoredavida.viewmodel.TemaApp
import com.example.apparvoredavida.model.BibleTranslation
import com.example.apparvoredavida.model.Livro
import com.example.apparvoredavida.model.Capitulo
import com.example.apparvoredavida.model.Versiculo
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    navController: NavController,
    book: String? = null,
    chapter: Int? = null,
    verse: Int? = null
) {
    val context = LocalContext.current
    val viewModel: BibliaViewModel = hiltViewModel()
    
    // Observando os StateFlows do ViewModel
    val traducoesDisponiveis by viewModel.traducoesDisponiveis.collectAsState()
    val traducaoSelecionada by viewModel.traducaoSelecionada.collectAsState()
    val nomesLivrosDisponiveis by viewModel.nomesLivrosDisponiveis.collectAsState()
    val livroCarregado by viewModel.livroCarregado.collectAsState()
    val capituloSelecionado by viewModel.capituloSelecionado.collectAsState()

    var showTraducoesSelector by remember { mutableStateOf(false) }
    var showLivrosSelector by remember { mutableStateOf(false) }
    var showCapitulosSelector by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(livroCarregado, capituloSelecionado, book, chapter, verse) {
        if (book != null && chapter != null && verse != null) {
            if (viewModel.traducaoSelecionada.value?.name == "ACF" && viewModel.livroCarregado.value?.nome == book) {
                val targetCapitulo = viewModel.livroCarregado.value?.capitulos?.find { it.numero == chapter }
                if (targetCapitulo != null) {
                    if (viewModel.capituloSelecionado.value?.numero != chapter) {
                        viewModel.selecionarCapitulo(targetCapitulo)
                    }
                    val verseIndex = targetCapitulo.versiculos.indexOfFirst { it.numero == verse }
                    if (verseIndex != -1) {
                        listState.animateScrollToItem(index = verseIndex)
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Bíblia",
            actions = {
                // Botão de traduções
                IconButton(onClick = { showTraducoesSelector = true }) {
                    Icon(Icons.Default.Translate, contentDescription = "Traduções")
                }
                // Botão de livros (mostra apenas se houver livros disponíveis)
                if (nomesLivrosDisponiveis.isNotEmpty()) {
                    IconButton(onClick = { showLivrosSelector = true }) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Livros")
                    }
                }
                // Botão de capítulos (mostra apenas se um livro estiver carregado)
                if (livroCarregado != null) {
                    IconButton(onClick = { showCapitulosSelector = true }) {
                        Icon(Icons.Default.List, contentDescription = "Capítulos")
                    }
                }
            }
        )

        // Conteúdo principal baseado no estado:
        when {
            // Mostrando seletor de traduções
            showTraducoesSelector -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(traducoesDisponiveis) { traducao ->
                        AppCard(
                            title = traducao.name,
                            description = traducao.jsonPath,
                            onClick = {
                                viewModel.selecionarTraducao(traducao)
                                showTraducoesSelector = false
                            }
                        )
                    }
                }
            }
            // Mostrando seletor de livros (se uma tradução estiver selecionada e houver nomes de livros)
            traducaoSelecionada != null && nomesLivrosDisponiveis.isNotEmpty() && showLivrosSelector -> {
                 LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(nomesLivrosDisponiveis) { nomeLivro ->
                        AppCard(
                            title = nomeLivro,
                            onClick = {
                                viewModel.definirNomeLivroParaCarregar(nomeLivro)
                                showLivrosSelector = false
                                showCapitulosSelector = true // Opcional: abrir seletor de capítulo automaticamente
                            }
                        )
                    }
                }
            }
            // Mostrando seletor de capítulos (se um livro estiver carregado)
            livroCarregado != null && showCapitulosSelector -> {
                 LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(livroCarregado!!.capitulos) { capitulo ->
                        AppCard(
                            title = "Capítulo ${capitulo.numero}",
                             onClick = {
                                viewModel.selecionarCapitulo(capitulo)
                                showCapitulosSelector = false
                            }
                        )
                    }
                }
            }
            // Exibindo versículos (se um capítulo estiver selecionado)
            capituloSelecionado != null -> {
                 LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(capituloSelecionado!!.versiculos) { versiculo ->
                       Text("${versiculo.numero}. ${versiculo.texto}")
                    }
                }
            }
            // Estado inicial ou carregando
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Pode adicionar um indicador de loading aqui, se necessário
                    Text("Selecione uma tradução, livro e capítulo")
                }
            }
        }
    }
} 