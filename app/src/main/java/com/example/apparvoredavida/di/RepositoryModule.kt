package com.example.apparvoredavida.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.apparvoredavida.data.FavoritesRepository
import com.example.apparvoredavida.data.repository.*
import com.example.apparvoredavida.data.repository.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(dataStore: DataStore<Preferences>): FavoritesRepository {
        return FavoritesRepository(dataStore)
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
} 