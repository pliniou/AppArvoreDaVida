package com.example.apparvoredavida.di

import android.content.Context
import androidx.room.Room
import com.example.apparvoredavida.data.bible.BibleDatabase
import com.example.apparvoredavida.data.bible.dao.BibleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para o banco de dados da Bíblia.
 * Gerencia a criação e acesso ao banco de dados SQLite.
 */
@Module
@InstallIn(SingletonComponent::class)
object BibleDatabaseModule {

    /**
     * Fornece uma instância do banco de dados da Bíblia.
     * O banco de dados é criado usando o Room e é configurado para permitir
     * consultas no thread principal (não recomendado para produção).
     */
    @Provides
    @Singleton
    fun provideBibleDatabase(
        @ApplicationContext context: Context
    ): BibleDatabase {
        return Room.databaseBuilder(
            context,
            BibleDatabase::class.java,
            "bible_database"
        )
        .allowMainThreadQueries() // Apenas para desenvolvimento
        .build()
    }

    /**
     * Fornece uma instância do DAO da Bíblia.
     * O DAO é obtido a partir da instância do banco de dados.
     */
    @Provides
    @Singleton
    fun provideBibleDao(bibleDatabase: BibleDatabase): BibleDao {
        return bibleDatabase.bibleDao()
    }
} 