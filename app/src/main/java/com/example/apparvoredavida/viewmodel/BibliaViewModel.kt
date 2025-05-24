package com.example.apparvoredavida.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparvoredavida.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.apparvoredavida.data.bible.*
import androidx.room.Room

/**
 * ViewModel responsável por gerenciar a funcionalidade da Bíblia.
 * Implementa acesso ao banco de dados SQLite e gerenciamento de versões.
 */
@HiltViewModel
class BibliaViewModel @Inject constructor(
    application: Application,
    private val dataStore: DataStore<Preferences>
) : AndroidViewModel(application) {

    private var currentDatabase: BibleDatabase? = null
    private var bibleDao: BibleDao? = null

    // Versões disponíveis
    val versoesDisponiveis: List<BibleVersion> = listOf(
        BibleVersion("Almeida Corrigida Fiel", "ACF.sqlite"),
        BibleVersion("Almeida Revista e Atualizada", "ARA.sqlite"),
        BibleVersion("Almeida Revista e Corrigida", "ARC.sqlite"),
        BibleVersion("Nova Almeida Atualizada", "NAA.sqlite"),
        BibleVersion("Nova Bíblia Viva", "NBV.sqlite"),
        BibleVersion("Nova Tradução na Linguagem de Hoje", "NTLH.sqlite")
    )

    private val versaoAbbrevToDbPath = versoesDisponiveis.associate { versao ->
        versao.dbPath.replace(".sqlite", "") to versao.dbPath
    }

    // Mapa de abreviações de livro
    private val bookAbbrevToRefId = mapOf(
        "GN" to 1, "ÊX" to 2, "LV" to 3, "NM" to 4, "DT" to 5, "JS" to 6, "JZ" to 7, "RT" to 8,
        "1SM" to 9, "2SM" to 10, "1RS" to 11, "2RS" to 12, "1CR" to 13, "2CR" to 14, "ED" to 15,
        "NE" to 16, "ET" to 17, "JÓ" to 18, "SL" to 19, "PV" to 20, "EC" to 21, "CT" to 22,
        "IS" to 23, "JR" to 24, "LM" to 25, "EZ" to 26, "DN" to 27, "OS" to 28, "JL" to 29,
        "AM" to 30, "OB" to 31, "JN" to 32, "MQ" to 33, "NA" to 34, "HC" to 35, "SF" to 36,
        "AG" to 37, "ZC" to 38, "ML" to 39, "MT" to 40, "MC" to 41, "LC" to 42, "JO" to 43,
        "AT" to 44, "RM" to 45, "1CO" to 46, "2CO" to 47, "GL" to 48, "EF" to 49, "FP" to 50,
        "CL" to 51, "1TS" to 52, "2TS" to 53, "1TM" to 54, "2TM" to 55, "TT" to 56, "FM" to 57,
        "HB" to 58, "TG" to 59, "1PE" to 60, "2PE" to 61, "1JO" to 62, "2JO" to 63, "3JO" to 64,
        "JD" to 65, "AP" to 66
    )

    private val _versaoSelecionada = MutableStateFlow(versoesDisponiveis.first())
    val versaoSelecionada: StateFlow<BibleVersion> = _versaoSelecionada.asStateFlow()

    private val _nomesLivrosDisponiveis = MutableStateFlow<List<String>>(emptyList())
    val nomesLivrosDisponiveis: StateFlow<List<String>> = _nomesLivrosDisponiveis.asStateFlow()

    private val _livroCarregado = MutableStateFlow<Livro?>(null)
    val livroCarregado: StateFlow<Livro?> = _livroCarregado.asStateFlow()

    private val _capituloSelecionado = MutableStateFlow<Capitulo?>(null)
    val capituloSelecionado: StateFlow<Capitulo?> = _capituloSelecionado.asStateFlow()

    private val _nomeLivroParaCarregar = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            carregarBancoDeDadosParaVersao(_versaoSelecionada.value.dbPath)
        }
    }

    /**
     * Seleciona uma versão da Bíblia e carrega seu banco de dados.
     * @param versao Versão a ser selecionada
     */
    fun selecionarVersao(versao: BibleVersion) {
        if (_versaoSelecionada.value.dbPath != versao.dbPath) {
            _versaoSelecionada.value = versao
            _livroCarregado.value = null
            _capituloSelecionado.value = null
            _nomeLivroParaCarregar.value = null
            _nomesLivrosDisponiveis.value = emptyList()
            viewModelScope.launch {
                carregarBancoDeDadosParaVersao(versao.dbPath)
            }
        }
    }

    /**
     * Define o livro a ser carregado.
     * @param nomeLivro Nome do livro a ser carregado
     */
    fun definirNomeLivroParaCarregar(nomeLivro: String) {
        _nomeLivroParaCarregar.value = nomeLivro
        _livroCarregado.value = null
        _capituloSelecionado.value = null
        viewModelScope.launch {
            carregarLivroEspecifico()
        }
    }

    /**
     * Seleciona um capítulo específico.
     * @param capitulo Capítulo a ser selecionado
     */
    fun selecionarCapitulo(capitulo: Capitulo?) {
        _capituloSelecionado.value = capitulo
    }

    /**
     * Carrega o banco de dados para uma versão específica.
     * @param dbFileName Nome do arquivo do banco de dados
     */
    private suspend fun carregarBancoDeDadosParaVersao(dbFileName: String) {
        currentDatabase?.close()
        currentDatabase = null
        bibleDao = null

        val databasePathInAssets = "databases/$dbFileName"

        try {
            val db = withContext(Dispatchers.IO) {
                Room.databaseBuilder(
                    getApplication<Application>().applicationContext,
                    BibleDatabase::class.java,
                    dbFileName
                )
                .createFromAsset(databasePathInAssets)
                .build()
            }
            currentDatabase = db
            bibleDao = db.bibleDao()
            Log.d("BibliaVM", "Banco de dados '$dbFileName' carregado com sucesso.")
            carregarNomesDosLivrosDaVersaoAtual()
        } catch (e: Exception) {
            Log.e("BibliaVM", "Erro ao carregar DB $dbFileName: ${e.message}")
        }
    }

    /**
     * Carrega os nomes dos livros da versão atual.
     */
    private fun carregarNomesDosLivrosDaVersaoAtual() {
        bibleDao?.let { dao ->
            viewModelScope.launch {
                _nomesLivrosDisponiveis.value = emptyList()
                try {
                    dao.getAllBooks().collect { bookEntities ->
                        val nomes = bookEntities.map { it.name }
                        _nomesLivrosDisponiveis.value = nomes
                        if (nomes.isNotEmpty()) {
                            Log.d("BibliaVM", "Nomes dos livros carregados do DB: ${nomes.size} livros.")
                        } else {
                            Log.w("BibliaVM", "Nenhum nome de livro carregado do DB.")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BibliaVM", "Erro ao carregar nomes dos livros do DB: ${e.message}")
                    _nomesLivrosDisponiveis.value = emptyList()
                }
            }
        } ?: run {
            Log.w("BibliaVM", "bibleDao é nulo ao tentar carregar nomes de livros.")
            _nomesLivrosDisponiveis.value = emptyList()
        }
    }

    /**
     * Carrega um livro específico do banco de dados.
     */
    private suspend fun carregarLivroEspecifico() {
        val nomeLivro = _nomeLivroParaCarregar.value ?: return

        bibleDao?.let { dao ->
            _livroCarregado.value = null
            Log.d("BibliaVM", "Carregando livro '$nomeLivro' do DB...")

            try {
                val bookEntity = dao.getBookByName(nomeLivro)

                if (bookEntity != null) {
                    dao.getVersesForBook(bookEntity.bookId).collect { versesForBook ->
                        val versiculosAgrupadosPorCapitulo = versesForBook.groupBy { it.chapter }
                        val capitulos = versiculosAgrupadosPorCapitulo.map { (capNum, verseEntities) ->
                            Capitulo(
                                numero = capNum,
                                versiculos = verseEntities.map { verseEntity ->
                                    Versiculo(
                                        numero = verseEntity.verseNumber,
                                        texto = verseEntity.text
                                    )
                                }.sortedBy { it.numero }
                            )
                        }.sortedBy { it.numero }

                        val livroCarregadoUI = Livro(
                            nome = bookEntity.name,
                            abreviacao = getBookAbbrev(bookEntity.bookReferenceId) ?: "",
                            capitulos = capitulos
                        )

                        _livroCarregado.value = livroCarregadoUI
                        Log.d("BibliaVM", "Livro '$nomeLivro' carregado do DB com ${capitulos.size} capítulos.")
                    }
                } else {
                    Log.w("BibliaVM", "Livro '$nomeLivro' NÃO encontrado no DB.")
                    _livroCarregado.value = null
                }
            } catch (e: Exception) {
                Log.e("BibliaVM", "Erro ao carregar livro '$nomeLivro' do DB: ${e.message}")
                _livroCarregado.value = null
            }
        } ?: run {
            Log.w("BibliaVM", "bibleDao é nulo ao tentar carregar livro.")
            _livroCarregado.value = null
        }
    }

    /**
     * Obtém a abreviação de um livro pelo seu ID de referência.
     * @param bookReferenceId ID de referência do livro
     * @return Abreviação do livro ou null se não encontrado
     */
    private fun getBookAbbrev(bookReferenceId: Int): String? {
        return bookAbbrevToRefId.entries.find { it.value == bookReferenceId }?.key
    }

    /**
     * Busca um versículo específico pelo seu ID.
     * @param verseId ID do versículo no formato VERSAO_ABREV_LIVRO_CAPITULO_VERSICULO
     * @return Detalhes do versículo ou null se não encontrado
     */
    suspend fun getVerseById(verseId: String): VersiculoDetails? {
        Log.d("BibliaVM", "Buscando versículo com ID: $verseId")
        val parts = verseId.split("_")
        if (parts.size != 5) {
            Log.e("BibliaVM", "Formato de verseId inválido. Esperado VERSAO_ABREV_LIVRO_CAPITULO_VERSICULO: $verseId")
            return null
        }

        val (versaoAbbrev, bookAbbrev, capStr, verStr, verseIdInVersao) = parts

        val dbPath = versaoAbbrevToDbPath[versaoAbbrev]
        if (dbPath == null) {
            Log.e("BibliaVM", "Abreviação de versão inválida no verseId: $versaoAbbrev")
            return null
        }

        if (_versaoSelecionada.value.dbPath != dbPath) {
            Log.d("BibliaVM", "Trocando para o banco de dados da versão: $versaoAbbrev")
            carregarBancoDeDadosParaVersao(dbPath)
            Log.d("BibliaVM", "Tentando prosseguir após carregar DB. Verificar se bibleDao está pronto.")
        }

        val bookReferenceId = bookAbbrevToRefId[bookAbbrev]
        if (bookReferenceId == null) {
            Log.e("BibliaVM", "Abreviação de livro inválida no verseId: $bookAbbrev")
            return null
        }

        val capNum = capStr.toIntOrNull()
        val verNum = verStr.toIntOrNull()
        if (capNum == null || verNum == null) {
            Log.e("BibliaVM", "Número de capítulo ou versículo inválido no verseId: $capStr, $verStr")
            return null
        }

        return bibleDao?.let { dao ->
            try {
                val bookEntity = dao.getBookByReferenceId(bookReferenceId)

                if (bookEntity != null) {
                    val verseEntity = dao.getSpecificVerse(bookEntity.bookId, capNum, verNum)

                    if (verseEntity != null) {
                        Log.d("BibliaVM", "Versículo encontrado: ${bookEntity.name} ${verseEntity.chapter}:${verseEntity.verseNumber}")
                        VersiculoDetails(
                            livroNome = bookEntity.name,
                            capituloNumero = verseEntity.chapter,
                            versiculoNumero = verseEntity.verseNumber,
                            texto = verseEntity.text,
                            id = verseId
                        )
                    } else {
                        Log.w("BibliaVM", "Versículo não encontrado para ID: $verseId (Book ID: ${bookEntity.bookId}, Cap: $capNum, Ver: $verNum)")
                        null
                    }
                } else {
                    Log.w("BibliaVM", "Livro não encontrado para reference ID: $bookReferenceId (do verseId $verseId)")
                    null
                }
            } catch (e: Exception) {
                Log.e("BibliaVM", "Erro ao buscar versículo com ID $verseId do DB: ${e.message}")
                e.printStackTrace()
                null
            }
        } ?: run {
            Log.e("BibliaVM", "bibleDao é nulo ao tentar buscar versículo por ID.")
            null
        }
    }

    override fun onCleared() {
        currentDatabase?.close()
        super.onCleared()
    }
} 