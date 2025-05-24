package com.example.apparvoredavida.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalContext
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.graphics.drawable.toBitmap

/**
 * Classe utilitária para gerenciar o carregamento de imagens com Coil.
 */
@Singleton
class ImageLoader @Inject constructor(
    private val context: Context
) {
    private val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 25% da memória disponível
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve(Constants.IMAGE_CACHE_DIR))
                    .maxSizePercent(0.02) // 2% do espaço disponível
                    .build()
            }
            .crossfade(true)
            .build()
    }

    init {
        Coil.setImageLoader(imageLoader)
    }

    /**
     * Carrega uma imagem do cache ou da rede.
     * @param url URL da imagem
     * @param placeholder Drawable a ser exibido durante o carregamento
     * @param error Drawable a ser exibido em caso de erro
     * @return Drawable da imagem carregada
     */
    suspend fun loadImage(
        url: String,
        placeholder: Drawable? = null,
        error: Drawable? = null
    ): Drawable? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .placeholder(placeholder)
                .error(error)
                .crossfade(true)
                .build()

            when (val result = imageLoader.execute(request)) {
                is SuccessResult -> result.drawable
                else -> error
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error
        }
    }

    /**
     * Carrega uma imagem como Bitmap.
     * @param url URL da imagem
     * @return Bitmap da imagem ou null em caso de erro
     */
    suspend fun loadBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()

            when (val result = imageLoader.execute(request)) {
                is SuccessResult -> result.drawable.toBitmap()
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Composable para carregar uma imagem com estado de carregamento.
     * @param url URL da imagem
     * @param placeholder Drawable a ser exibido durante o carregamento
     * @param error Drawable a ser exibido em caso de erro
     * @param onSuccess Callback chamado quando a imagem é carregada com sucesso
     * @param onError Callback chamado quando ocorre um erro
     * @return State<Drawable?> com o estado da imagem
     */
    @Composable
    fun rememberImageState(
        url: String,
        placeholder: Drawable? = null,
        error: Drawable? = null,
        onSuccess: (Drawable) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ): State<Drawable?> {
        val context = LocalContext.current
        val imageLoader = Coil.imageLoader(context)
        val imageState = remember { mutableStateOf<Drawable?>(placeholder) }

        LaunchedEffect(url) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .placeholder(placeholder)
                    .error(error)
                    .crossfade(true)
                    .build()

                when (val result = imageLoader.execute(request)) {
                    is SuccessResult -> {
                        imageState.value = result.drawable
                        onSuccess(result.drawable)
                    }
                    else -> {
                        imageState.value = error
                        onError(Exception("Falha ao carregar imagem"))
                    }
                }
            } catch (e: Exception) {
                imageState.value = error
                onError(e)
            }
        }

        return imageState
    }
} 