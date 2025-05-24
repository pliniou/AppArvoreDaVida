package com.example.apparvoredavida.di

import android.content.Context
import android.content.res.AssetManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.apparvoredavida.data.bible.BibleDatabase
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Módulo de injeção de dependência principal do aplicativo.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager {
        return AssetManager(context.assets)
    }

    @Provides
    @Singleton
    fun provideCacheManager(@ApplicationContext context: Context): CacheManager {
        return CacheManager(context)
    }

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }

    @Provides
    @Singleton
    fun providePdfLoader(@ApplicationContext context: Context): PdfLoader {
        return PdfLoader(context)
    }

    @Provides
    @Singleton
    fun provideBibleDatabase(@ApplicationContext context: Context): BibleDatabase {
        return Room.databaseBuilder(
            context,
            BibleDatabase::class.java,
            "bible.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBibleDao(database: BibleDatabase): BibleDao {
        return database.bibleDao()
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(dataStore: DataStore<Preferences>): FavoritesRepository {
        return FavoritesRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        assetManager: AssetManager,
        errorHandler: ErrorHandler
    ): MusicRepository {
        return MusicRepositoryImpl(context, assetManager, errorHandler)
    }

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

    @Provides
    @Singleton
    fun providePreferencesRepository(dataStore: DataStore<Preferences>): PreferencesRepository {
        return PreferencesRepositoryImpl(dataStore)
    }
} 