package com.example.apparvoredavida.data.bible.repository

import com.example.apparvoredavida.data.bible.entity.BookEntity
import com.example.apparvoredavida.data.bible.entity.TestamentEntity
import com.example.apparvoredavida.data.bible.entity.TranslationMetadataEntity
import com.example.apparvoredavida.data.bible.entity.VerseEntity
import kotlinx.coroutines.flow.Flow

interface BibleRepository {
    // Metadados
    fun getTranslationMetadata(): Flow<TranslationMetadataEntity?>
    
    // Livros
    fun getAllBooks(): Flow<List<BookEntity>>
    suspend fun getBookById(bookId: Int): BookEntity?
    suspend fun getBookByName(bookName: String): BookEntity?
    suspend fun getBookByReferenceId(bookReferenceId: Int): BookEntity?
    
    // Versículos
    fun getVersesForChapter(bookId: Int, chapterNumber: Int): Flow<List<VerseEntity>>
    suspend fun getSpecificVerse(bookId: Int, chapterNumber: Int, verseNo: Int): VerseEntity?
    fun getVersesForBook(bookId: Int): Flow<List<VerseEntity>>
    
    // Migração
    suspend fun migrateFromJsonIfNeeded()
} 