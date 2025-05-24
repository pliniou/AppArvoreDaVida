package com.example.apparvoredavida.di

import android.content.Context
import com.example.apparvoredavida.data.repository.ScoreRepository
import com.example.apparvoredavida.data.repository.impl.ScoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para a feature de Partituras.
 */
@Module
@InstallIn(SingletonComponent::class)
object ScoreModule {

    @Provides
    @Singleton
    fun provideScoreRepository(
        @ApplicationContext context: Context
    ): ScoreRepository {
        return ScoreRepositoryImpl(context)
    }
} 