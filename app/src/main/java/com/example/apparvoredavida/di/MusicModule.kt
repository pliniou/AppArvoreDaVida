package com.example.apparvoredavida.di

import com.example.apparvoredavida.data.repository.MusicRepository
import com.example.apparvoredavida.data.repository.impl.MusicRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para a feature de Músicas.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MusicModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository
} 