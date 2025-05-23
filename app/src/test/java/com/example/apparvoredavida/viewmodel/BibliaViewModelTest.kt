package com.example.apparvoredavida.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.apparvoredavida.data.bible.BibleDatabase
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.bible.entity.BookEntity
import com.example.apparvoredavida.data.bible.entity.VerseEntity
import com.example.apparvoredavida.model.BibleTranslation
import com.example.apparvoredavida.model.Capitulo
import com.example.apparvoredavida.model.Livro
import com.example.apparvoredavida.model.Versiculo
import com.example.apparvoredavida.model.VersiculoDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.mockito.Mockito.mock
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.apparvoredavida.data.bible.entity.TranslationMetadataEntity
import com.example.apparvoredavida.data.bible.entity.TestamentEntity
import org.mockito.`when` // Importação correta para 'when'
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.*

// Para rodar testes unitários que dependem do contexto Android (como Room)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class BibliaViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: BibleDatabase
    private lateinit var bibleDao: BibleDao
    private lateinit var viewModel: BibliaViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var mockDataStore: DataStore<Preferences>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = ApplicationProvider.getApplicationContext<Application>()
        mockDataStore = mock(DataStore::class.java) as DataStore<Preferences>

        // Configurar o banco de dados em memória
        database = Room.inMemoryDatabaseBuilder(
            application.applicationContext,
            BibleDatabase::class.java
        )
        .allowMainThreadQueries() // Permite queries na thread principal para testes simples
        .build()

        bibleDao = database.bibleDao()

        // Mockar o comportamento do DataStore se necessário, ou usar um impl fake
        // Por enquanto, vamos apenas instanciar o ViewModel com o mock

        // Instanciar o ViewModel. Precisamos mockar as dependências.
        viewModel = BibliaViewModel(application, mockDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun carregarNomesDosLivrosDaTraducaoAtual_deveRetornarNomesCorretos() = runTest {
        // Inserir dados de teste no banco de dados em memória
        val book1 = BookEntity(bookId = 1, bookReferenceId = 1, testamentReferenceId = 1, name = "Gênesis")
        val book2 = BookEntity(bookId = 2, bookReferenceId = 2, testamentReferenceId = 1, name = "Êxodo")
        
        // Para inserir Books, precisamos inserir Testaments primeiro devido à FK
        val oldTestament = TestamentEntity(testamentId = 1, name = "Antigo Testamento")
        bibleDao.insertTestament(oldTestament) // Adicionar método insertTestament no DAO se não existir

        bibleDao.insertBook(book1) // Adicionar método insertBook no DAO se não existir
        bibleDao.insertBook(book2) // Adicionar método insertBook no DAO se não existir

        // Chamar a função a ser testada
        viewModel.carregarNomesDosLivrosDaTraducaoAtual()
        
        // Coletar o Flow e verificar o resultado
        val nomesLivros = viewModel.nomesLivrosDisponiveis.first()

        // Verificar se a lista de nomes é a esperada
        assert(nomesLivros == listOf("Gênesis", "Êxodo"))
    }

    @Test
    fun carregarLivroEspecifico_deveRetornarLivroComCapitulosEVersiculosCorretos() = runTest {
        // Inserir dados de teste
        val testament = TestamentEntity(testamentId = 1, name = "Antigo Testamento")
        val book = BookEntity(bookId = 1, bookReferenceId = 1, testamentReferenceId = 1, name = "Gênesis")
        val verse1_1 = VerseEntity(verseId = 1, bookId = 1, chapter = 1, verseNumber = 1, text = "Texto Gênesis 1:1")
        val verse1_2 = VerseEntity(verseId = 2, bookId = 1, chapter = 1, verseNumber = 2, text = "Texto Gênesis 1:2")
        val verse2_1 = VerseEntity(verseId = 3, bookId = 1, chapter = 2, verseNumber = 1, text = "Texto Gênesis 2:1")

        bibleDao.insertTestament(testament)
        bibleDao.insertBook(book)
        bibleDao.insertVerse(verse1_1)
        bibleDao.insertVerse(verse1_2)
        bibleDao.insertVerse(verse2_1)

        // Simular a seleção do livro no ViewModel
        viewModel.definirNomeLivroParaCarregar("Gênesis")

        // Aguardar a carga do livro (a função é suspend e atualiza um StateFlow)
        // Podemos avançar o dispatcher para garantir que a coroutine termine
        testDispatcher.scheduler.advanceUntilIdle()

        // Coletar o Flow do livro carregado
        val livroCarregado = viewModel.livroCarregado.first()

        // Verificar se o livro foi carregado corretamente
        assert(livroCarregado != null)
        assert(livroCarregado?.nome == "Gênesis")
        assert(livroCarregado?.capitulos?.size == 2) // Esperamos 2 capítulos

        // Verificar o conteúdo do primeiro capítulo
        val capitulo1 = livroCarregado?.capitulos?.find { it.numero == 1 }
        assert(capitulo1 != null)
        assert(capitulo1?.versiculos?.size == 2) // Esperamos 2 versículos no capítulo 1
        assert(capitulo1?.versiculos?.get(0)?.numero == 1)
        assert(capitulo1?.versiculos?.get(0)?.texto == "Texto Gênesis 1:1")
        assert(capitulo1?.versiculos?.get(1)?.numero == 2)
        assert(capitulo1?.versiculos?.get(1)?.texto == "Texto Gênesis 1:2")

        // Verificar o conteúdo do segundo capítulo
        val capitulo2 = livroCarregado?.capitulos?.find { it.numero == 2 }
        assert(capitulo2 != null)
        assert(capitulo2?.versiculos?.size == 1) // Esperamos 1 versículo no capítulo 2
        assert(capitulo2?.versiculos?.get(0)?.numero == 1)
        assert(capitulo2?.versiculos?.get(0)?.texto == "Texto Gênesis 2:1")
    }

    @Test
    fun getVerseById_deveRetornarDetalhesCorretosParaIdValido() = runTest {
        // Inserir dados de teste: Testamento, Livro e Versículo
        val testament = TestamentEntity(testamentId = 1, name = "Antigo Testamento")
        val book = BookEntity(bookId = 1, bookReferenceId = 1, testamentReferenceId = 1, name = "Gênesis")
        val verse = VerseEntity(verseId = 101, bookId = 1, chapter = 1, verseNumber = 1, text = "No princípio criou Deus os céus e a terra.")

        bibleDao.insertTestament(testament)
        bibleDao.insertBook(book)
        bibleDao.insertVerse(verse)

        // Definir um ID de versículo no formato esperado
        // Formato: TRADUCAO_ABREV_LIVRO_CAPITULO_VERSICULO
        val testVerseId = "ACF_GN_1_1_101"

        // Chamar a função a ser testada
        val versiculoDetails = viewModel.getVerseById(testVerseId)

        // Verificar se os detalhes do versículo foram retornados corretamente
        assert(versiculoDetails != null)
        assert(versiculoDetails?.id == testVerseId)
        assert(versiculoDetails?.livroNome == "Gênesis")
        assert(versiculoDetails?.capituloNumero == 1)
        assert(versiculoDetails?.versiculoNumero == 1)
        assert(versiculoDetails?.texto == "No princípio criou Deus os céus e a terra.")
    }

    @Test
    fun getVerseById_deveRetornarNullParaIdInvalido() = runTest {
        // Inserir dados de teste (um versículo, mas o ID buscado será diferente)
        val testament = TestamentEntity(testamentId = 1, name = "Antigo Testamento")
        val book = BookEntity(bookId = 1, bookReferenceId = 1, testamentReferenceId = 1, name = "Gênesis")
        val verse = VerseEntity(verseId = 101, bookId = 1, chapter = 1, verseNumber = 1, text = "No princípio criou Deus os céus e a terra.")

        bibleDao.insertTestament(testament)
        bibleDao.insertBook(book)
        bibleDao.insertVerse(verse)

        // Definir IDs de versículo inválidos
        val invalidIdFormat = "ACF-GN-1-1-101" // Formato incorreto
        val nonExistentVerseId = "ACF_GN_1_99_101" // Versículo inexistente
        val nonExistentBookId = "ACF_EX_1_1_201" // Livro inexistente (Book Reference ID 2 para Êxodo não inserido)

        // Testar ID com formato inválido
        val detailsInvalidFormat = viewModel.getVerseById(invalidIdFormat)
        assert(detailsInvalidFormat == null)

        // Testar ID de versículo inexistente
        val detailsNonExistentVerse = viewModel.getVerseById(nonExistentVerseId)
        assert(detailsNonExistentVerse == null)

        // Testar ID de livro inexistente
        val detailsNonExistentBook = viewModel.getVerseById(nonExistentBookId)
        assert(detailsNonExistentBook == null)
    }

    // TODO: Adicionar testes para getVerseById
} 