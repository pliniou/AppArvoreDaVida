package com.example.apparvoredavida.di

import android.content.Context
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.CacheManager
import com.example.apparvoredavida.util.ErrorHandler
import com.example.apparvoredavida.util.MusicLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para componentes relacionados a música.
 */
@Module
@InstallIn(SingletonComponent::class)
object MusicModule {

    @Provides
    @Singleton
    fun provideMusicLoader(
        @ApplicationContext context: Context,
        assetManager: AssetManager,
        cacheManager: CacheManager,
        errorHandler: ErrorHandler
    ): MusicLoader {
        return MusicLoader(context, assetManager, cacheManager, errorHandler)
    }
} 