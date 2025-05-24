package com.example.apparvoredavida.di

import android.content.Context
import androidx.room.Room
import com.example.apparvoredavida.data.bible.BibleDatabase
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.bible.repository.BibleRepository
import com.example.apparvoredavida.data.bible.repository.BibleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência para a feature da Bíblia.
 */
@Module
@InstallIn(SingletonComponent::class)
object BibleModule {

    @Provides
    @Singleton
    fun provideBibleDatabase(
        @ApplicationContext context: Context
    ): BibleDatabase {
        return Room.databaseBuilder(
            context,
            BibleDatabase::class.java,
            "bible_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBibleDao(database: BibleDatabase): BibleDao {
        return database.bibleDao()
    }

    @Provides
    @Singleton
    fun provideBibleRepository(
        bibleDao: BibleDao,
        @ApplicationContext context: Context
    ): BibleRepository {
        return BibleRepositoryImpl(bibleDao, context)
    }
} 