package com.example.apparvoredavida.di

import android.content.Context
import com.example.apparvoredavida.data.datastore.PdfPreferences
import com.example.apparvoredavida.data.repository.PdfRepository
import com.example.apparvoredavida.data.repository.impl.PdfRepositoryImpl
import com.example.apparvoredavida.util.AssetManager
import com.example.apparvoredavida.util.ErrorHandler
import com.example.apparvoredavida.util.PdfLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para componentes relacionados a PDF.
 */
@Module
@InstallIn(SingletonComponent::class)
object PdfModule {

    @Provides
    @Singleton
    fun providePdfPreferences(
        @ApplicationContext context: Context
    ): PdfPreferences {
        return PdfPreferences(context)
    }

    @Provides
    @Singleton
    fun providePdfLoader(
        @ApplicationContext context: Context,
        assetManager: AssetManager,
        errorHandler: ErrorHandler
    ): PdfLoader {
        return PdfLoader(context, assetManager, errorHandler)
    }

    @Provides
    @Singleton
    fun providePdfRepository(
        @ApplicationContext context: Context,
        pdfPreferences: PdfPreferences,
        assetManager: AssetManager,
        errorHandler: ErrorHandler
    ): PdfRepository {
        return PdfRepositoryImpl(context, pdfPreferences, assetManager, errorHandler)
    }
} 