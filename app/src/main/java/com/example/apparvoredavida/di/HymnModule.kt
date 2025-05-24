package com.example.apparvoredavida.di

import com.example.apparvoredavida.data.repository.HymnRepository
import com.example.apparvoredavida.data.repository.impl.HymnRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para a feature de Hinos.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HymnModule {

    @Binds
    @Singleton
    abstract fun bindHymnRepository(
        hymnRepositoryImpl: HymnRepositoryImpl
    ): HymnRepository
} 