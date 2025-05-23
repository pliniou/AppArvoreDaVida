package com.example.apparvoredavida.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("configuracoes") }
        )

    // Exemplo de repositório
    @Provides
    @Singleton
    fun provideExampleRepository(): ExampleRepository = ExampleRepositoryImpl()
}

// Exemplo de interface e implementação de repositório
interface ExampleRepository {
    fun getExample(): String
}

class ExampleRepositoryImpl : ExampleRepository {
    override fun getExample(): String = "Exemplo do Hilt"
} 