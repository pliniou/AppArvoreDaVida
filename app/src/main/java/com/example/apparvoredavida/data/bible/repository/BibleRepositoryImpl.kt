package com.example.apparvoredavida.data.bible.repository

import android.content.Context
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.bible.entity.BookEntity
import com.example.apparvoredavida.data.bible.entity.TestamentEntity
import com.example.apparvoredavida.data.bible.entity.TranslationMetadataEntity
import com.example.apparvoredavida.data.bible.entity.VerseEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    private val bibleDao: BibleDao,
    @ApplicationContext private val context: Context
) : BibleRepository {

    override fun getTranslationMetadata(): Flow<TranslationMetadataEntity?> = 
        bibleDao.getTranslationMetadata()

    override fun getAllBooks(): Flow<List<BookEntity>> = 
        bibleDao.getAllBooks()

    override suspend fun getBookById(bookId: Int): BookEntity? = 
        bibleDao.getBookById(bookId)

    override suspend fun getBookByName(bookName: String): BookEntity? = 
        bibleDao.getBookByName(bookName)

    override suspend fun getBookByReferenceId(bookReferenceId: Int): BookEntity? = 
        bibleDao.getBookByReferenceId(bookReferenceId)

    override fun getVersesForChapter(bookId: Int, chapterNumber: Int): Flow<List<VerseEntity>> = 
        bibleDao.getVersesForChapter(bookId, chapterNumber)

    override suspend fun getSpecificVerse(bookId: Int, chapterNumber: Int, verseNo: Int): VerseEntity? = 
        bibleDao.getSpecificVerse(bookId, chapterNumber, verseNo)

    override fun getVersesForBook(bookId: Int): Flow<List<VerseEntity>> = 
        bibleDao.getVersesForBook(bookId)

    override suspend fun migrateFromJsonIfNeeded() = withContext(Dispatchers.IO) {
        // Verifica se já existe metadados no banco
        val metadata = bibleDao.getTranslationMetadata().first()
        if (metadata == null) {
            // Se não existir, faz a migração
            migrateFromJson()
        }
    }

    private suspend fun migrateFromJson() = withContext(Dispatchers.IO) {
        try {
            // Lê o arquivo JSON de metadados
            val metadataJson = context.assets.open("bible/metadata.json").bufferedReader().use { it.readText() }
            val metadata = Json.decodeFromString<TranslationMetadataEntity>(metadataJson)
            
            // Lê o arquivo JSON de livros
            val booksJson = context.assets.open("bible/books.json").bufferedReader().use { it.readText() }
            val books = Json.decodeFromString<List<BookEntity>>(booksJson)
            
            // Lê o arquivo JSON de versículos
            val versesJson = context.assets.open("bible/verses.json").bufferedReader().use { it.readText() }
            val verses = Json.decodeFromString<List<VerseEntity>>(versesJson)
            
            // Insere os dados no banco
            books.forEach { book ->
                bibleDao.insertBook(book)
            }
            
            verses.forEach { verse ->
                bibleDao.insertVerse(verse)
            }
            
            Timber.d("Migração concluída com sucesso")
            
        } catch (e: Exception) {
            Timber.e(e, "Erro durante a migração dos dados")
        }
    }
} 