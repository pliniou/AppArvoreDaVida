package com.example.apparvoredavida.di

import android.content.Context
import com.example.apparvoredavida.data.datastore.PdfPreferences
import com.example.apparvoredavida.data.repository.PdfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
    fun providePdfRepository(
        @ApplicationContext context: Context,
        pdfPreferences: PdfPreferences
    ): PdfRepository {
        return PdfRepository(context, pdfPreferences)
    }
} 