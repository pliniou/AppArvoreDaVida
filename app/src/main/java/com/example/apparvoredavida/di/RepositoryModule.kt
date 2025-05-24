package com.example.apparvoredavida.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.repository.*
import com.example.apparvoredavida.data.repository.impl.*
import com.example.apparvoredavida.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para os repositórios.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBibleRepository(
        @ApplicationContext context: Context,
        bibleDao: BibleDao
    ): BibleRepository {
        return BibleRepositoryImpl(context, bibleDao)
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        assetManager: AssetManager,
        cacheManager: CacheManager,
        errorHandler: ErrorHandler
    ): MusicRepository {
        return MusicRepositoryImpl(context, assetManager, cacheManager, errorHandler)
    }

    @Provides
    @Singleton
    fun provideHymnRepository(
        @ApplicationContext context: Context,
        assetManager: AssetManager,
        errorHandler: ErrorHandler
    ): HymnRepository {
        return HymnRepositoryImpl(context, assetManager, errorHandler)
    }

    @Provides
    @Singleton
    fun providePartituraRepository(
        @ApplicationContext context: Context,
        assetManager: AssetManager,
        errorHandler: ErrorHandler
    ): PartituraRepository {
        return PartituraRepositoryImpl(context, assetManager, errorHandler)
    }
} 