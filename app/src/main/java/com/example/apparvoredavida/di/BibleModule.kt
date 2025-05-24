package com.example.apparvoredavida.di

import com.example.apparvoredavida.data.repository.BibleRepository
import com.example.apparvoredavida.data.repository.impl.BibleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para a feature da Bíblia.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BibleModule {

    @Binds
    @Singleton
    abstract fun bindBibleRepository(
        bibleRepositoryImpl: BibleRepositoryImpl
    ): BibleRepository
} 