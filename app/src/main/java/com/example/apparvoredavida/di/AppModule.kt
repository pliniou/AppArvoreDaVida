package com.example.apparvoredavida.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.apparvoredavida.data.bible.BibleDatabase
import com.example.apparvoredavida.data.bible.BibleDao
import com.example.apparvoredavida.data.repository.*
import com.example.apparvoredavida.data.repository.impl.*
import com.example.apparvoredavida.util.PdfLoader
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
    fun providePdfLoader(@ApplicationContext context: Context): PdfLoader {
        return PdfLoader(context)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(dataStore: DataStore<Preferences>): FavoritesRepository {
        return FavoritesRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideMusicRepository(@ApplicationContext context: Context): MusicRepository {
        return MusicRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideBibleRepository(@ApplicationContext context: Context): BibleRepository {
        return BibleRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideHymnRepository(@ApplicationContext context: Context): HymnRepository {
        return HymnRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun providePartituraRepository(@ApplicationContext context: Context): PartituraRepository {
        return PartituraRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(dataStore: DataStore<Preferences>): PreferencesRepository {
        return PreferencesRepositoryImpl(dataStore)
    }
} 