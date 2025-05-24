package com.example.apparvoredavida.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import com.example.apparvoredavida.data.repository.PreferencesRepository
import com.example.apparvoredavida.model.TamanhoFonte
import com.example.apparvoredavida.util.Constants
import com.example.apparvoredavida.util.PdfLoader
import android.util.Log

/**
 * ViewModel responsável por gerenciar a visualização de documentos.
 * Implementa configurações de zoom, rotação e outras preferências de visualização.
 */
@HiltViewModel
class VisualizadorViewModel @Inject constructor(
    application: Application,
    private val preferencesRepository: PreferencesRepository
) : AndroidViewModel(application) {
    
    private val pdfLoader = PdfLoader(application)
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var tempFile: File? = null
    private var currentFileId: String? = null

    private val _zoom = MutableStateFlow(1.0f)
    val zoom: StateFlow<Float> = _zoom.asStateFlow()

    private val _rotation = MutableStateFlow(0)
    val rotation: StateFlow<Int> = _rotation.asStateFlow()

    private val _fontSize = MutableStateFlow(TamanhoFonte.MEDIO)
    val fontSize: StateFlow<TamanhoFonte> = _fontSize.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(0)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentBitmap = MutableStateFlow<Bitmap?>(null)
    val currentBitmap: StateFlow<Bitmap?> = _currentBitmap.asStateFlow()

    init {
        viewModelScope.launch {
            loadPreferences()
        }
    }

    /**
     * Carrega as preferências de visualização do DataStore.
     */
    private suspend fun loadPreferences() {
        try {
            _fontSize.value = preferencesRepository.getFontSize()
            _zoom.value = preferencesRepository.getZoom()
            _rotation.value = preferencesRepository.getRotation()
        } catch (e: Exception) {
            Log.e("VisualizadorVM", "Erro ao carregar preferências: ${e.message}")
        }
    }

    /**
     * Define o nível de zoom.
     * @param newZoom Novo valor de zoom
     */
    fun setZoom(newZoom: Float) {
        viewModelScope.launch {
            try {
                preferencesRepository.setZoom(newZoom)
                _zoom.value = newZoom
            } catch (e: Exception) {
                // TODO: Implementar tratamento de erro adequado
            }
        }
    }

    /**
     * Define a rotação do documento.
     * @param degrees Ângulo de rotação em graus
     */
    fun setRotation(degrees: Int) {
        viewModelScope.launch {
            try {
                preferencesRepository.setRotation(degrees)
                _rotation.value = degrees
            } catch (e: Exception) {
                // TODO: Implementar tratamento de erro adequado
            }
        }
    }

    /**
     * Define o tamanho da fonte.
     * @param size Novo tamanho de fonte
     */
    fun setFontSize(size: TamanhoFonte) {
        viewModelScope.launch {
            try {
                preferencesRepository.setFontSize(size)
                _fontSize.value = size
            } catch (e: Exception) {
                // TODO: Implementar tratamento de erro adequado
            }
        }
    }

    // Função para atualizar a página atual (chamada do listener da View)
    fun updateCurrentPage(page: Int) {
        _currentPage.value = page
    }

    // Função para atualizar o total de páginas (chamada do listener da View)
    fun updateTotalPages(total: Int) {
        _totalPages.value = total
    }

    // Função para definir mensagem de erro
    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    // TODO: Implementar lógica para carregar PDF (usando PdfLoader, etc.)
    // TODO: Implementar lógica para favoritar/compartilhar (usando repositórios)

    // TODO: Implementar função para compartilhar o PDF atual
    fun sharePdf(filePath: String) {
        // Lógica para compartilhar o arquivo PDF
    }

    // TODO: Implementar função para adicionar/remover o PDF atual dos favoritos
    fun toggleFavorite(filePath: String) {
        // Lógica para adicionar ou remover o PDF dos favoritos (usando DataStore/repositório)
    }

    // Função para navegar para a próxima página
    fun goToNextPage() {
        if (_currentPage.value < _totalPages.value - 1) {
            _currentPage.value++
        }
    }

    // Função para navegar para a página anterior
    fun goToPreviousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun loadFile(fileId: String, fileType: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                currentFileId = fileId
                
                // Limpa recursos anteriores
                closeCurrentPdf()
                
                // Carrega o arquivo do assets
                val assetPath = when (fileType) {
                    "pdf" -> {
                        // Determina o diretório correto baseado no ID do arquivo
                        val dir = if (fileId.startsWith("hino")) {
                            Constants.DIR_HINARIO
                        } else {
                            Constants.DIR_PARTITURAS
                        }
                        "$dir/$fileId${Constants.EXTENSION_PDF}"
                    }
                    else -> throw IllegalArgumentException("Tipo de arquivo não suportado: $fileType")
                }

                // Carrega a primeira página usando o PdfLoader
                val screenWidth = getApplication<Application>().resources.displayMetrics.widthPixels
                val bitmap = pdfLoader.loadPdfPage(assetPath, 0, screenWidth)
                
                if (bitmap != null) {
                    _currentBitmap.value = bitmap
                    _currentPage.value = 0
                    
                    // Abre o PDF para obter o total de páginas
                    val fileDescriptor = ParcelFileDescriptor.open(
                        File(getApplication<Application>().cacheDir, "temp_pdf"),
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                    pdfRenderer = PdfRenderer(fileDescriptor)
                    _totalPages.value = pdfRenderer?.pageCount ?: 0
                } else {
                    throw Exception("Não foi possível carregar o arquivo PDF")
                }
                
            } catch (e: Exception) {
                Log.e("VisualizadorVM", "Erro ao carregar arquivo: ${e.message}")
                _errorMessage.value = "Erro ao carregar arquivo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setPage(pageIndex: Int) {
        viewModelScope.launch {
            try {
                if (pageIndex < 0 || pageIndex >= (_totalPages.value)) {
                    throw IllegalArgumentException("Página inválida: $pageIndex")
                }

                _isLoading.value = true
                
                // Fecha a página atual
                currentPage?.close()
                
                // Carrega a nova página usando o PdfLoader
                val screenWidth = getApplication<Application>().resources.displayMetrics.widthPixels
                val fileId = currentFileId ?: throw IllegalStateException("Nenhum arquivo carregado")
                val bitmap = pdfLoader.loadPdfPage(
                    "${Constants.DIR_PARTITURAS}/$fileId${Constants.EXTENSION_PDF}",
                    pageIndex,
                    screenWidth
                )
                
                if (bitmap != null) {
                    _currentBitmap.value = bitmap
                    _currentPage.value = pageIndex
                } else {
                    throw Exception("Não foi possível carregar a página $pageIndex")
                }
                
            } catch (e: Exception) {
                Log.e("VisualizadorVM", "Erro ao mudar página: ${e.message}")
                _errorMessage.value = "Erro ao mudar página: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun closeCurrentPdf() {
        currentPage?.close()
        currentPage = null
        pdfRenderer?.close()
        pdfRenderer = null
        tempFile?.delete()
        tempFile = null
        _currentBitmap.value = null
        currentFileId = null
    }

    override fun onCleared() {
        closeCurrentPdf()
        super.onCleared()
    }
} 