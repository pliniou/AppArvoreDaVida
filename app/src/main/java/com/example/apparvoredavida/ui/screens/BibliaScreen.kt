package com.example.apparvoredavida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apparvoredavida.ui.components.AppTopBar
import com.example.apparvoredavida.ui.components.AppCard
import com.example.apparvoredavida.viewmodel.BibliaViewModel
import com.example.apparvoredavida.model.BibleTranslation
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
    val viewModel: BibliaViewModel = hiltViewModel()
    
    val traducoesDisponiveis = viewModel.traducoesDisponiveis
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
            if (viewModel.traducaoSelecionada.value.name == "ACF" && viewModel.livroCarregado.value?.nome == book) {
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
                IconButton(onClick = { showTraducoesSelector = true }) {
                    Icon(Icons.Default.Translate, contentDescription = "Traduções")
                }
                if (nomesLivrosDisponiveis.isNotEmpty()) {
                    IconButton(onClick = { showLivrosSelector = true }) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Livros")
                    }
                }
                if (livroCarregado != null) {
                    IconButton(onClick = { showCapitulosSelector = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Capítulos")
                    }
                }
            }
        )

        when {
            showTraducoesSelector -> TraducoesSelector(
                traducoes = traducoesDisponiveis,
                onTraducaoSelected = {
                    viewModel.selecionarTraducao(it)
                    showTraducoesSelector = false
                }
            )
            traducaoSelecionada != null && nomesLivrosDisponiveis.isNotEmpty() && showLivrosSelector -> LivrosSelector(
                nomesLivros = nomesLivrosDisponiveis,
                onLivroSelected = {
                    viewModel.definirNomeLivroParaCarregar(it)
                    showLivrosSelector = false
                    showCapitulosSelector = true
                }
            )
            livroCarregado != null && showCapitulosSelector -> CapitulosSelector(
                capitulos = livroCarregado!!.capitulos,
                onCapituloSelected = {
                    viewModel.selecionarCapitulo(it)
                    showCapitulosSelector = false
                }
            )
            capituloSelecionado != null -> VersiculosList(
                versiculos = capituloSelecionado!!.versiculos,
                listState = listState
            )
            else -> EmptyState()
        }
    }
}

@Composable
private fun TraducoesSelector(
    traducoes: List<BibleTranslation>,
    onTraducaoSelected: (BibleTranslation) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(traducoes) { traducao ->
            AppCard(
                title = traducao.name,
                description = traducao.dbPath,
                onClick = { onTraducaoSelected(traducao) }
            )
        }
    }
}

@Composable
private fun LivrosSelector(
    nomesLivros: List<String>,
    onLivroSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(nomesLivros) { nomeLivro ->
            AppCard(
                title = nomeLivro,
                onClick = { onLivroSelected(nomeLivro) }
            )
        }
    }
}

@Composable
private fun CapitulosSelector(
    capitulos: List<Capitulo>,
    onCapituloSelected: (Capitulo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(capitulos) { capitulo ->
            AppCard(
                title = "Capítulo ${capitulo.numero}",
                onClick = { onCapituloSelected(capitulo) }
            )
        }
    }
}

@Composable
private fun VersiculosList(
    versiculos: List<Versiculo>,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(versiculos) { versiculo ->
            Text("${versiculo.numero}. ${versiculo.texto}")
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Selecione uma tradução, livro e capítulo")
    }
} 