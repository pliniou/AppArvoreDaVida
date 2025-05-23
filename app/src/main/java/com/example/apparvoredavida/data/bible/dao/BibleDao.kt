package com.example.apparvoredavida.data.bible.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apparvoredavida.data.bible.entity.BookEntity
import com.example.apparvoredavida.data.bible.entity.TranslationMetadataEntity
import com.example.apparvoredavida.data.bible.entity.VerseEntity
import kotlinx.coroutines.flow.Flow
import com.example.apparvoredavida.data.bible.entity.TestamentEntity

@Dao
interface BibleDao {
    // --- Metadados da Tradução ---
    @Query("SELECT * FROM metadata LIMIT 1") // Assumindo um arquivo SQLite por tradução
    fun getTranslationMetadata(): Flow<TranslationMetadataEntity?> // Ou direto TranslationMetadataEntity se sempre houver uma linha

    // --- Livros ---
    @Query("SELECT * FROM book ORDER BY book_reference_id ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM book WHERE id = :bookId")
    suspend fun getBookById(bookId: Int): BookEntity?

    @Query("SELECT * FROM book WHERE name = :bookName LIMIT 1") // Para buscar por nome completo
    suspend fun getBookByName(bookName: String): BookEntity?

    @Query("SELECT * FROM book WHERE book_reference_id = :bookReferenceId LIMIT 1")
    suspend fun getBookByReferenceId(bookReferenceId: Int): BookEntity?

    // --- Versículos ---
    @Query("SELECT * FROM verse WHERE book_id = :bookId AND chapter = :chapterNumber ORDER BY verse ASC")
    fun getVersesForChapter(bookId: Int, chapterNumber: Int): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verse WHERE book_id = :bookId AND chapter = :chapterNumber AND verse = :verseNo LIMIT 1")
    suspend fun getSpecificVerse(bookId: Int, chapterNumber: Int, verseNo: Int): VerseEntity?

    @Query("SELECT * FROM verse WHERE book_id = :bookId ORDER BY chapter ASC, verse ASC")
    fun getVersesForBook(bookId: Int): Flow<List<VerseEntity>>

    // Poderíamos ter uma query mais complexa para buscar um VerseEntity pelo ID de favorito (TRAD_BOOK_CHAP_VERSE)
    // mas isso exigiria parsear o ID de favorito e então chamar getSpecificVerse.
    // Ou, se você adicionar colunas 'book_abbrev' e 'translation_id' à tabela 'verse' no SQLite,
    // a busca direta seria mais fácil.

    // --- Métodos de Inserção (para testes e preenchimento inicial) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestament(testament: TestamentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerse(verse: VerseEntity)
} 