package com.example.apparvoredavida.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparvoredavida.model.BibleTranslation
import com.example.apparvoredavida.model.Livro
import com.example.apparvoredavida.model.Capitulo
import com.example.apparvoredavida.model.Versiculo
import com.example.apparvoredavida.model.VersiculoDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import android.util.Log
import kotlinx.serialization.decodeFromString
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.apparvoredavida.data.bible.BibleDatabase // Importar o banco de dados
import com.example.apparvoredavida.data.bible.dao.BibleDao // Importar o DAO
import androidx.room.Room // Importar Room explicitamente
import kotlinx.coroutines.flow.first // Importar first para Flow
import com.example.apparvoredavida.data.bible.entity.VerseEntity // Importar VerseEntity
import com.example.apparvoredavida.data.bible.entity.BookEntity // Importar BookEntity para o mapa

@HiltViewModel
class BibliaViewModel @Inject constructor(
    application: Application,
    private val dataStore: DataStore<Preferences> // Injetar DataStore
    // O BibleDatabase e BibleDao serão gerenciados DENTRO do ViewModel dinamicamente
) : AndroidViewModel(application) {

    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    private var currentDatabase: BibleDatabase? = null // Referência para a instância do banco de dados ativo
    private var bibleDao: BibleDao? = null // Referência para o DAO ativo

    // Traduções disponíveis (manter por enquanto, pode ser carregado do DB metadata depois)
    val traducoesDisponiveis: List<BibleTranslation> = listOf(
        BibleTranslation("Almeida Corrigida Fiel", "ACF.json"),
        BibleTranslation("Almeida Revista e Atualizada", "ARA.json"),
        BibleTranslation("Almeida Revista e Corrigida", "ARC.json"),
        BibleTranslation("Nova Almeida Atualizada", "NAA.json"),
        BibleTranslation("Nova Bíblia Viva", "NBV.json"),
        BibleTranslation("Nova Tradução na Linguagem de Hoje", "NTLH.json")
    )

    // Mapa de abreviações de tradução para jsonPath
    private val traducaoAbbrevToJsonPath = traducoesDisponiveis.associate { traducao ->
        // Assumindo que a abreviação é a parte antes do ".json"
        traducao.jsonPath.replace(".json", "") to traducao.jsonPath
    }

    // Mapa de abreviações de livro no verseId para book_reference_id
    // Este mapa DEVE corresponder aos book_reference_id usados no banco de dados SQLite
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

    private val _traducaoSelecionada = MutableStateFlow(traducoesDisponiveis.first())
    val traducaoSelecionada: StateFlow<BibleTranslation> = _traducaoSelecionada.asStateFlow()

    // Nomes dos livros da tradução atualmente selecionada (para o seletor de livros)
    private val _nomesLivrosDisponiveis = MutableStateFlow<List<String>>(emptyList())
    val nomesLivrosDisponiveis: StateFlow<List<String>> = _nomesLivrosDisponiveis.asStateFlow()

    // O Livro atualmente carregado e selecionado (pode precisar ser adaptado/substituído)
    private val _livroCarregado = MutableStateFlow<Livro?>(null)
    val livroCarregado: StateFlow<Livro?> = _livroCarregado.asStateFlow()

    // O capítulo selecionado do livro carregado (pode precisar ser adaptado/substituído)
    private val _capituloSelecionado = MutableStateFlow<Capitulo?>(null)
    val capituloSelecionado: StateFlow<Capitulo?> = _capituloSelecionado.asStateFlow()

    // Para guardar o nome do livro que o usuário quer carregar
    private val _nomeLivroParaCarregar = MutableStateFlow<String?>(null)


    init {
        // Carregar nomes dos livros da tradução padrão ao iniciar
        // Esta chamada precisará ser modificada para usar o DB após ele ser carregado
        // TODO: Remover lógica de carregamento de JSON comentada (feito implicitamente, não há código JSON comentado)
        // Carregar a tradução padrão ao iniciar o ViewModel
        viewModelScope.launch {
             carregarBancoDeDadosParaTraducao(_traducaoSelecionada.value.jsonPath) // jsonPath contém o nome do arquivo .sqlite
        }
    }

    fun selecionarTraducao(traducao: BibleTranslation) {
        if (_traducaoSelecionada.value.jsonPath != traducao.jsonPath) {
            _traducaoSelecionada.value = traducao
            _livroCarregado.value = null
            _capituloSelecionado.value = null
            _nomeLivroParaCarregar.value = null
            _nomesLivrosDisponiveis.value = emptyList() // Limpar antes de carregar novos
            // Chamar função para carregar o novo banco de dados
            viewModelScope.launch {
                carregarBancoDeDadosParaTraducao(traducao.jsonPath) // jsonPath contém o nome do arquivo .sqlite
            }
        }
    }

    // Chamado pela UI quando um nome de livro é escolhido no seletor
    fun definirNomeLivroParaCarregar(nomeLivro: String) {
        _nomeLivroParaCarregar.value = nomeLivro
        _livroCarregado.value = null // Indicar que um novo livro será carregado
        _capituloSelecionado.value = null
        // Chamar função para carregar o livro do DB
        viewModelScope.launch {
             carregarLivroEspecifico()
        }
    }

    fun selecionarCapitulo(capitulo: Capitulo?) {
        _capituloSelecionado.value = capitulo
    }

    // Função para carregar/mudar a instância do banco de dados
    private suspend fun carregarBancoDeDadosParaTraducao(dbFileName: String) {
        // Fechar banco de dados anterior se existir
        currentDatabase?.close()
        currentDatabase = null
        bibleDao = null

        // Construir o novo banco de dados. Assume que o arquivo .sqlite está em assets/databases/
        // O nome do arquivo jsonPath (ex: "ARA.json") precisa ser adaptado para o nome do arquivo sqlite (ex: "ARA.sqlite")
        val sqliteFileName = dbFileName.replace(".json", ".sqlite")
        val databasePathInAssets = "databases/$sqliteFileName" // Assumindo que os arquivos .sqlite estão em assets/databases/

        try {
             val db = withContext(Dispatchers.IO) {
                 Room.databaseBuilder(
                     getApplication<Application>().applicationContext,
                     BibleDatabase::class.java,
                     sqliteFileName // O nome do arquivo no sistema de arquivos do dispositivo
                 )
                // Se for a primeira vez que o app acessa este DB, ele copiará de assets.
                // Você pode precisar usar createFromAsset ou createFromInputStream dependendo de como os DBs são empacotados.
                // createFromAsset assume que o arquivo no path databasePathInAssets é a fonte.
                // createFromAsset PRECISA do caminho RELATIVO dentro de 'assets'.
                 .createFromAsset(databasePathInAssets)
                 .build()
             }
            currentDatabase = db
            bibleDao = db.bibleDao()
            Log.d("BibliaVM", "Banco de dados para '$sqliteFileName' carregado com sucesso.")

            // Ao carregar o DB, devemos carregar os nomes dos livros SOMENTE DEPOIS que o DB estiver pronto.
            // A função carregarNomesDosLivrosDaTraducaoAtual já tem a verificação bibleDao?.let { ... }
            // então podemos chamá-la após bibleDao ser atribuído.
             carregarNomesDosLivrosDaTraducaoAtual()

        } catch (e: IOException) {
            Log.e("BibliaVM", "Erro IO ao carregar DB $sqliteFileName: ${e.message}")
            // Lidar com erro: talvez definir um estado de erro na UI
        } catch (e: Exception) {
            Log.e("BibliaVM", "Erro ao carregar DB $sqliteFileName: ${e.message}")
            // Lidar com erro
        }
    }

    private fun carregarNomesDosLivrosDaTraducaoAtual() {
        // Esta função agora depende de bibleDao não ser nulo
        bibleDao?.let { dao ->
            viewModelScope.launch {
                _nomesLivrosDisponiveis.value = emptyList() // Mostrar que está carregando
                Log.d("BibliaVM", "Carregando nomes dos livros do DB...")
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
                     // Lidar com erro
                }
            }
        } ?: run {
             Log.w("BibliaVM", "bibleDao é nulo ao tentar carregar nomes de livros.")
             _nomesLivrosDisponiveis.value = emptyList()
        }
    }

    private suspend fun carregarLivroEspecifico() {
        val nomeLivro = _nomeLivroParaCarregar.value ?: return

        bibleDao?.let { dao ->
            _livroCarregado.value = null // Indicar que está carregando
            Log.d("BibliaVM", "Carregando livro '$nomeLivro' do DB...")

            try {
                val bookEntity = dao.getBookByName(nomeLivro)

                if (bookEntity != null) {
                    // Buscar TODOS os versículos para este livro usando o novo método
                    dao.getVersesForBook(bookEntity.bookId).collect { versesForBook ->
                        // Agrupar versículos por número de capítulo
                        val versiculosAgrupadosPorCapitulo = versesForBook.groupBy { it.chapter }

                        // Mapear para lista de objetos Capitulo
                        val capitulos = versiculosAgrupadosPorCapitulo.map { (capNum, verseEntities) ->
                            Capitulo(
                                numero = capNum,
                                // Mapear VerseEntity para Versiculo simples (numero, texto)
                                versiculos = verseEntities.map { verseEntity ->
                                    com.example.apparvoredavida.model.Versiculo(
                                        numero = verseEntity.verseNumber,
                                        texto = verseEntity.text
                                    )
                                }.sortedBy { it.numero }
                            )
                        }.sortedBy { it.numero }

                        // Criar o objeto Livro para o estado da UI
                        val livroCarregadoUI = Livro(
                            nome = bookEntity.name,
                            abreviacao = getBookAbbrev(bookEntity.bookReferenceId) ?: "", // Obter abreviação do mapa
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
                // Lidar com erro
            }

        } ?: run {
            Log.w("BibliaVM", "bibleDao é nulo ao tentar carregar livro.")
            _livroCarregado.value = null
        }
    }

    // Função auxiliar para obter a abreviação do livro pelo book_reference_id
    private fun getBookAbbrev(bookReferenceId: Int): String? {
        return bookAbbrevToRefId.entries.find { it.value == bookReferenceId }?.key
    }

    // Implementar getVerseById usando o Room Database
    suspend fun getVerseById(verseId: String): VersiculoDetails? {
        Log.d("BibliaVM", "Buscando versículo com ID: $verseId")
        val parts = verseId.split("_")
        if (parts.size != 5) { // Ajustado para esperar 5 partes
            Log.e("BibliaVM", "Formato de verseId inválido. Esperado TRADUCAO_ABREV_LIVRO_CAPITULO_VERSICULO: $verseId")
            return null
        }

        val (tradAbbrev, bookAbbrev, capStr, verStr, verseIdInTrad) = parts // Adicionado verseIdInTrad para consistência, embora não usado na busca Room

        // 1. Obter jsonPath da tradução
        val jsonPath = traducaoAbbrevToJsonPath[tradAbbrev]
        if (jsonPath == null) {
            Log.e("BibliaVM", "Abreviação de tradução inválida no verseId: $tradAbbrev")
            return null
        }

        // 2. Garantir que o banco de dados correto esteja carregado
        // Se a tradução selecionada atualmente não for a do verseId, carregar o DB correto.
        // Nota: carregarBancoDeDadosParaTraducao é suspend, então precisamos estar em um contexto suspend.
        if (_traducaoSelecionada.value.jsonPath != jsonPath) {
            Log.d("BibliaVM", "Trocando para o banco de dados da tradução: $tradAbbrev")
            carregarBancoDeDadosParaTraducao(jsonPath)
            // Aguardar um pouco para garantir que o DB foi carregado? Ou usar um mecanismo mais robusto?
            // Para simplificar agora, vamos prosseguir, assumindo que a carga é rápida ou síncrona o suficiente aqui.
            // Em um cenário real, talvez fosse melhor retornar null e forçar a UI a esperar.
            // Ou, idealmente, carregarBancoDeDadosParaTraducao poderia emitir um estado indicando quando está pronto.
            // Por enquanto, vamos apenas adicionar um log e prosseguir.
            Log.d("BibliaVM", "Tentando prosseguir após carregar DB. Verificar se bibleDao está pronto.")
        }

        // 3. Obter o book_reference_id pela abreviação do livro
        val bookReferenceId = bookAbbrevToRefId[bookAbbrev]
        if (bookReferenceId == null) {
            Log.e("BibliaVM", "Abreviação de livro inválida no verseId: $bookAbbrev")
            return null
        }

        // 4. Parsear capítulo e versículo
        val capNum = capStr.toIntOrNull()
        val verNum = verStr.toIntOrNull()
        if (capNum == null || verNum == null) {
            Log.e("BibliaVM", "Número de capítulo ou versículo inválido no verseId: $capStr, $verStr")
            return null
        }

        // 5. Usar o DAO para buscar o versículo
        // Precisamos garantir que bibleDao não seja nulo APÓS a potencial carga do DB
        return bibleDao?.let { dao ->
            try {
                // Buscar o BookEntity para obter o nome completo do livro
                // Nota: getBookByReferenceId é mais robusto pois usa um ID padrão
                val bookEntity = dao.getBookByReferenceId(bookReferenceId)

                if (bookEntity != null) {
                    val verseEntity = dao.getSpecificVerse(bookEntity.bookId, capNum, verNum)

                    if (verseEntity != null) {
                        Log.d("BibliaVM", "Versículo encontrado: ${bookEntity.name} ${verseEntity.chapter}:${verseEntity.verseNumber}") // Log mais detalhado
                        VersiculoDetails(
                            livroNome = bookEntity.name, // Usar nome do livro do BookEntity
                            capituloNumero = verseEntity.chapter,
                            versiculoNumero = verseEntity.verseNumber,
                            texto = verseEntity.text,
                            id = verseId // Manter o ID original passado
                        )
                    } else {
                        Log.w("BibliaVM", "Versículo não encontrado para ID: $verseId (Book ID: ${bookEntity.bookId}, Cap: $capNum, Ver: $verNum)") // Log mais detalhado
                        null
                    }
                } else {
                    Log.w("BibliaVM", "Livro não encontrado para reference ID: $bookReferenceId (do verseId $verseId)")
                    null
                }
            } catch (e: Exception) {
                Log.e("BibliaVM", "Erro ao buscar versículo com ID $verseId do DB: ${e.message}")
                e.printStackTrace() // Adicionar printStackTrace para melhor depuração
                null
            }
        } ?: run {
            Log.e("BibliaVM", "bibleDao é nulo ao tentar buscar versículo por ID.")
            null
        }
    }

    // TODO: Adicionar função para fechar o banco de dados quando o ViewModel não for mais necessário (onCleared)

     // Remover funções de leitura de JSON que não serão mais usadas
    // private suspend fun lerJsonDeAssets(context: Context, path: String): String {...}
    // private suspend fun <T> comContextoIO(bloco: suspend () -> T): T {...}

    override fun onCleared() {
        currentDatabase?.close()
        super.onCleared()
    }
} 