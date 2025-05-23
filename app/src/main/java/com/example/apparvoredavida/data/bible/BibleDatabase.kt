package com.example.apparvoredavida.data.bible

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.bible.entity.BookEntity
import com.example.apparvoredavida.data.bible.entity.TestamentEntity
import com.example.apparvoredavida.data.bible.entity.TranslationMetadataEntity
import com.example.apparvoredavida.data.bible.entity.VerseEntity

@Database(
    entities = [
        TranslationMetadataEntity::class,
        BookEntity::class,
        VerseEntity::class,
        TestamentEntity::class
    ],
    version = 1, // Comece com 1 e incremente se o esquema mudar
    exportSchema = false // Geralmente false em apps pequenos, true para gerar arquivo JSON do esquema
)
abstract class BibleDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao
    // Você pode adicionar mais DAOs aqui se necessário
} 