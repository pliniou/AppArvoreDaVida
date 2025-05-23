package com.example.apparvoredavida.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import com.example.apparvoredavida.model.Album
import com.example.apparvoredavida.model.Music
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class MusicaViewModelTest {
    private lateinit var application: Application
    private lateinit var context: Context
    private lateinit var assetManager: AssetManager
    private lateinit var viewModel: MusicaViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk()
        context = mockk()
        assetManager = mockk()
        every { application.applicationContext } returns context
        every { context.assets } returns assetManager
        viewModel = MusicaViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `expandirAlbum should toggle album expansion`() = runTest {
        // Arrange
        val albumId = "test-album"

        // Act
        viewModel.expandirAlbum(albumId)
        val firstState = viewModel.albumExpandido.value
        viewModel.expandirAlbum(albumId)
        val secondState = viewModel.albumExpandido.value

        // Assert
        assertEquals(albumId, firstState)
        assertNull(secondState)
    }

    @Test
    fun `alternarVisualizacao should toggle between GRID and LISTA`() = runTest {
        // Arrange
        val initialState = viewModel.visualizacao.value

        // Act
        viewModel.alternarVisualizacao()
        val secondState = viewModel.visualizacao.value
        viewModel.alternarVisualizacao()
        val thirdState = viewModel.visualizacao.value

        // Assert
        assertEquals(VisualizacaoMusica.GRID, initialState)
        assertEquals(VisualizacaoMusica.LISTA, secondState)
        assertEquals(VisualizacaoMusica.GRID, thirdState)
    }

    @Test
    fun `carregarMusicas should load music files from assets`() = runTest {
        // Arrange
        val musicFiles = listOf(
            "album1/song1.mp3",
            "album1/song2.mp3",
            "album2/song1.mp3"
        )
        every { assetManager.list("mp3") } returns musicFiles.toTypedArray()

        // Act
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val albuns = viewModel.albuns.value
        assertEquals(2, albuns.size)
        assertEquals("album1", albuns[0].name)
        assertEquals(2, albuns[0].songs.size)
        assertEquals("album2", albuns[1].name)
        assertEquals(1, albuns[1].songs.size)
    }
} 