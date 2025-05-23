package com.example.apparvoredavida.util

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.apparvoredavida.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.os.Build
import android.util.Log

class MusicLoader(private val context: Context) {
    private val cacheDir = File(context.cacheDir, "music_cache").apply { mkdirs() }
    private val metadataCache = mutableMapOf<String, Music>()
    private var exoPlayer: ExoPlayer? = null
    private var playerListener: Player.Listener? = null

    init {
        exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context))
            .build()
    }

    suspend fun loadMusicMetadata(assetPath: String): Music? = withContext(Dispatchers.IO) {
        // Verifica cache
        metadataCache[assetPath]?.let { return@withContext it }

        val retriever = MediaMetadataRetriever()
        var tempFile: File? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Usar AssetFileDescriptor diretamente para APIs 29+
                val afd = context.assets.openFd(assetPath)
                afd.use { // Usar use para fechar o descriptor
                    retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.declaredLength)
                }
            } else {
                // Copiar para arquivo temporário para APIs mais antigas
                val inputStream = context.assets.open(assetPath)
                tempFile = File(cacheDir, "temp_music_${assetPath.replace("/", "_")}") // Criar no cacheDir
                FileOutputStream(tempFile).use { it.write(inputStream.readBytes()) }
                retriever.setDataSource(tempFile.absolutePath)
            }

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: File(assetPath).nameWithoutExtension
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L

            val music = Music(
                id = assetPath,
                title = title,
                artist = artist,
                album = null,
                duration = duration,
                path = assetPath,
                coverPath = null // Metadados de capa não são triviais de extrair de MP3 assets
            )

            metadataCache[assetPath] = music
            music
        } catch (e: Exception) {
            Log.e("MusicLoader", "Erro ao carregar metadados de música: $assetPath", e)
            null
        } finally {
            retriever.release()
            tempFile?.delete() // Limpar arquivo temporário
        }
    }

    fun prepareMusic(music: Music) {
        val mediaItem = MediaItem.fromUri("asset:///${music.path}")
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        playerListener?.onMediaItemTransition(mediaItem, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun stop() {
        exoPlayer?.stop()
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun setPlayerEventListener(listener: Player.Listener) {
        playerListener = listener
        exoPlayer?.addListener(listener)
    }

    fun removePlayerEventListener(listener: Player.Listener) {
        exoPlayer?.removeListener(listener)
        if (playerListener == listener) {
            playerListener = null
        }
    }

    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0L
    }

    fun clearCache() {
        metadataCache.clear()
        cacheDir.listFiles()?.forEach { it.delete() }
    }
} 