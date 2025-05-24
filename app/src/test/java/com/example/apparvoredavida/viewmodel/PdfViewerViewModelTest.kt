package com.example.apparvoredavida.viewmodel

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import com.example.apparvoredavida.data.repository.PdfRepository
import com.example.apparvoredavida.model.PdfViewerUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PdfViewerViewModelTest {

    private lateinit var viewModel: PdfViewerViewModel
    private lateinit var pdfRepository: PdfRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        pdfRepository = mockk()
        viewModel = PdfViewerViewModel(pdfRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPdf deve atualizar o estado corretamente quando o PDF é carregado com sucesso`() = runTest {
        // Arrange
        val pdfName = "test.pdf"
        val mockRenderer = mockk<PdfRenderer>()
        val mockBitmap = mockk<Bitmap>()

        coEvery { pdfRepository.loadPdfFromAsset(pdfName) } returns mockRenderer
        every { mockRenderer.pageCount } returns 5
        every { pdfRepository.renderPage(0, 1080, 1920) } returns mockBitmap

        // Act
        viewModel.loadPdf(pdfName)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(5, state.pageCount)
        assertNull(state.errorMessage)
    }

    @Test
    fun `loadPdf deve atualizar o estado com erro quando falha ao carregar o PDF`() = runTest {
        // Arrange
        val pdfName = "test.pdf"
        val errorMessage = "Erro ao carregar PDF"
        coEvery { pdfRepository.loadPdfFromAsset(pdfName) } throws Exception(errorMessage)

        // Act
        viewModel.loadPdf(pdfName)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.errorMessage)
    }

    @Test
    fun `navigateToPage deve atualizar o estado corretamente`() = runTest {
        // Arrange
        val pageIndex = 1
        val mockBitmap = mockk<Bitmap>()
        every { pdfRepository.renderPage(pageIndex, 1080, 1920) } returns mockBitmap

        // Act
        viewModel.navigateToPage(pageIndex)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assertEquals(pageIndex, state.currentPageIndex)
        assertFalse(state.isPageLoading)
    }

    @Test
    fun `updateZoomLevel deve atualizar o nível de zoom corretamente`() = runTest {
        // Arrange
        val zoomLevel = 1.5f

        // Act
        viewModel.updateZoomLevel(zoomLevel)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assertEquals(zoomLevel, state.zoomLevel)
        coVerify { pdfRepository.saveZoomLevel(any(), zoomLevel) }
    }
} 