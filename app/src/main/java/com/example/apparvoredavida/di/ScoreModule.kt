package com.example.apparvoredavida.di

import com.example.apparvoredavida.data.repository.ScoreRepository
import com.example.apparvoredavida.data.repository.impl.ScoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para a feature de Partituras.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ScoreModule {

    @Binds
    @Singleton
    abstract fun bindScoreRepository(
        scoreRepositoryImpl: ScoreRepositoryImpl
    ): ScoreRepository
} 