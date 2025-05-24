package com.example.apparvoredavida.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.bible.dao.impl.BibleDaoImpl
import com.example.apparvoredavida.data.bible.entity.Book
import com.example.apparvoredavida.data.bible.entity.Translation
import com.example.apparvoredavida.data.bible.entity.Verse
import com.example.apparvoredavida.data.bible.entity.VerseDetails
import com.example.apparvoredavida.data.bible.repository.BibleRepository
import com.example.apparvoredavida.data.bible.repository.impl.BibleRepositoryImpl
import com.example.apparvoredavida.data.repository.HymnRepository
import com.example.apparvoredavida.data.repository.impl.HymnRepositoryImpl
import com.example.apparvoredavida.data.repository.MusicRepository
import com.example.apparvoredavida.data.repository.ScoreRepository
import com.example.apparvoredavida.data.repository.impl.MusicRepositoryImpl
import com.example.apparvoredavida.data.repository.impl.ScoreRepositoryImpl
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.CacheManager
import com.example.apparvoredavida.util.ErrorHandler
import com.example.apparvoredavida.util.PdfLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    fun provideBibleDao(@ApplicationContext context: Context): BibleDao {
        return BibleDaoImpl(context)
    }

    @Provides
    @Singleton
    fun providePdfLoader(@ApplicationContext context: Context): PdfLoader {
        return PdfLoader(context)
    }

    @Provides
    @Singleton
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager {
        return AssetManager(context)
    }

    @Provides
    @Singleton
    fun provideCacheManager(dataStore: DataStore<Preferences>): CacheManager {
        return CacheManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideErrorHandler(@ApplicationContext context: Context): ErrorHandler {
        return ErrorHandler(context)
    }
} 