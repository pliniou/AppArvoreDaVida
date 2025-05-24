package com.example.apparvoredavida.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparvoredavida.data.MusicMetadataCache
import com.example.apparvoredavida.model.Album
import com.example.apparvoredavida.model.Music
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.MusicLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.update
import androidx.media3.common.Player
import androidx.media3.common.MediaItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.apparvoredavida.data.repository.MusicRepository
import com.example.apparvoredavida.data.repository.FavoritesRepository

/**
 * ViewModel responsável por gerenciar a funcionalidade de músicas.
 * Implementa carregamento, reprodução e gerenciamento de favoritos.
 */
@HiltViewModel
class MusicaViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    private val musicLoader = MusicLoader(getApplication())
    private val metadataCache = MusicMetadataCache(getApplication())
    
    private val _albuns = MutableStateFlow<List<Album>>(emptyList())
    val albuns: StateFlow<List<Album>> = _albuns.asStateFlow()

    private val _albumMusicsMap = MutableStateFlow<Map<String, List<Music>>>(emptyMap())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _albumExpandido = MutableStateFlow<String?>(null)
    val albumExpandido: StateFlow<String?> = _albumExpandido.asStateFlow()

    private val _visualizacao = MutableStateFlow(VisualizacaoMusica.GRID)
    val visualizacao: StateFlow<VisualizacaoMusica> = _visualizacao.asStateFlow()

    private val _musicaSelecionada = MutableStateFlow<Music?>(null)
    val musicaSelecionada: StateFlow<Music?> = _musicaSelecionada.asStateFlow()

    // --- Novos estados para o player ---
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var updatePositionJob: Job? = null
    // ------------------------------------

    private val _musics = MutableStateFlow<List<Music>>(emptyList())
    val musics: StateFlow<List<Music>> = _musics.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    init {
        loadMusics()
        loadFavorites()
        // Observar o estado do player no musicLoader e atualizar os StateFlows
        musicLoader.setPlayerEventListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startPositionUpdates()
                } else {
                    stopPositionUpdates()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = musicLoader.getDuration() // Supondo que MusicLoader tenha getDuration()
                }
            }
             override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _currentPosition.value = 0L // Reset position on track change
                 _duration.value = 0L // Reset duration on track change, will be updated in STATE_READY
             }
        })
    }

    /**
     * Carrega a lista de músicas disponíveis.
     */
    private suspend fun loadMusics() {
        try {
            _musics.value = musicRepository.getAllMusics()
        } catch (e: Exception) {
            // TODO: Implementar tratamento de erro adequado
        }
    }

    /**
     * Carrega a lista de músicas favoritas.
     */
    private suspend fun loadFavorites() {
        try {
            _favorites.value = favoritesRepository.getFavoriteMusics()
        } catch (e: Exception) {
            // TODO: Implementar tratamento de erro adequado
        }
    }

    /**
     * Alterna o status de favorito de uma música.
     * @param musicId ID da música
     */
    fun toggleFavorite(musicId: String) {
        viewModelScope.launch {
            try {
                if (_favorites.value.contains(musicId)) {
                    favoritesRepository.removeFavoriteMusic(musicId)
                } else {
                    favoritesRepository.addFavoriteMusic(musicId)
                }
                loadFavorites()
            } catch (e: Exception) {
                // TODO: Implementar tratamento de erro adequado
            }
        }
    }

    /**
     * Verifica se uma música está nos favoritos.
     * @param musicId ID da música
     * @return true se a música está nos favoritos, false caso contrário
     */
    fun isFavorite(musicId: String): Boolean {
        return _favorites.value.contains(musicId)
    }

    private fun loadMusics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val musicFiles = AssetManager.listMusicFiles(getApplication())
                val musics = mutableListOf<Music>()
                
                // Carrega metadados do cache primeiro
                metadataCache.getAllMusicMetadata().collect { cachedMusics ->
                    musics.addAll(cachedMusics)
                }
                
                // Carrega metadados faltantes e atualiza cache
                val newMusics = musicFiles.mapNotNull { file ->
                    if (!musics.any { it.path == file }) {
                         musicLoader.loadMusicMetadata(file)?.also { music ->
                            musics.add(music)
                            metadataCache.saveMusicMetadata(music)
                        }
                    } else null
                }

                // Group by album
                val albumGroups = musics.groupBy { it.album ?: "Sem Álbum" }

                // Create Album objects
                val albums = albumGroups.map { (albumName, songs) ->
                    Album(
                        id = albumName, // Use album name as ID for simplicity
                        title = albumName, // Use album name as title
                        coverPath = songs.firstOrNull()?.coverPath,
                    )
                }.sortedBy { it.title }
                
                _albuns.value = albums
                _albumMusicsMap.value = albumGroups

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAlbumById(albumId: String): StateFlow<Pair<Album?, List<Music>>> {
        return _albuns.map { listaDeAlbuns ->
             val album = listaDeAlbuns.find { it.id == albumId }
             val musics = _albumMusicsMap.value[albumId] ?: emptyList()
            Pair(album, musics)
        }.stateIn(viewModelScope, SharingStarted.Lazily, Pair(null, emptyList()))
    }

    fun selecionarMusica(musica: Music) {
        _musicaSelecionada.value = musica
    }

    fun playMusic(music: Music) {
        viewModelScope.launch {
             musicLoader.prepareMusic(music) // Prepara a música
             musicLoader.play() // Inicia a reprodução
             _musicaSelecionada.value = music // Atualiza música selecionada
             startPositionUpdates() // Inicia a atualização da posição
        }
    }

    fun pauseMusic() {
        musicLoader.pause()
        stopPositionUpdates() // Para a atualização da posição ao pausar
    }

    fun stopMusic() {
        musicLoader.stop()
        stopPositionUpdates() // Para a atualização da posição ao parar
        _currentPosition.value = 0L // Reseta a posição ao parar
        _isPlaying.value = false // Atualiza estado de reprodução
    }

    private fun startPositionUpdates() {
        updatePositionJob?.cancel()
        updatePositionJob = viewModelScope.launch {
            while (true) {
                _currentPosition.value = musicLoader.getCurrentPosition() // Supondo que MusicLoader tenha getCurrentPosition()
                delay(200L) // Atualiza a cada 200ms
            }
        }
    }

    private fun stopPositionUpdates() {
        updatePositionJob?.cancel()
        updatePositionJob = null
    }

    override fun onCleared() {
        super.onCleared()
        musicLoader.release()
        stopPositionUpdates() // Garante que o job seja cancelado ao limpar o ViewModel
    }

    fun expandirAlbum(albumId: String) {
        _albumExpandido.value = if (_albumExpandido.value == albumId) null else albumId
    }

    fun toggleVisualizacao() {
        _visualizacao.value = if (_visualizacao.value == VisualizacaoMusica.GRID) {
            VisualizacaoMusica.LISTA
        } else {
            VisualizacaoMusica.GRID
        }
    }

    fun getMusicById(musicId: String): Music? {
        // Flatten the map of album to list of music and find the music by ID
        return _albumMusicsMap.value.values.flatten().find { it.id == musicId }
    }
} 