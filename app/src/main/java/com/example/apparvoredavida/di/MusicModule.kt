package com.example.apparvoredavida.di

import android.content.Context
import com.example.apparvoredavida.data.repository.MusicRepository
import com.example.apparvoredavida.data.repository.impl.MusicRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para a feature de Músicas.
 */
@Module
@InstallIn(SingletonComponent::class)
object MusicModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context
    ): MusicRepository {
        return MusicRepositoryImpl(context)
    }
} 