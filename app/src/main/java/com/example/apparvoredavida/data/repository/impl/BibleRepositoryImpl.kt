package com.example.apparvoredavida.data.repository.impl

import android.content.Context
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.repository.BibleRepository
import com.example.apparvoredavida.model.BibleTranslation
import com.example.apparvoredavida.model.Livro
import com.example.apparvoredavida.model.VersiculoDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório da Bíblia.
 * Gerencia o acesso aos dados da Bíblia usando o DAO e assets.
 */
@Singleton
class BibleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bibleDao: BibleDao
) : BibleRepository {

    private val _currentTranslation = MutableStateFlow<BibleTranslation?>(null)

    override suspend fun getTranslations(): List<BibleTranslation> {
        // TODO: Implementar carregamento das traduções disponíveis
        return emptyList()
    }

    override suspend fun getBooks(): List<Livro> {
        return _currentTranslation.value?.let { translation ->
            bibleDao.getBooks(translation.dbPath)
        } ?: emptyList()
    }

    override suspend fun getVerseDetails(
        translationId: String,
        bookName: String,
        chapter: Int,
        verse: Int
    ): VersiculoDetails? {
        return bibleDao.getVerseDetails(translationId, bookName, chapter, verse)
    }

    override suspend fun getChapterText(
        translationId: String,
        bookName: String,
        chapter: Int
    ): String? {
        return bibleDao.getChapterText(translationId, bookName, chapter)
    }

    override fun observeCurrentTranslation(): Flow<BibleTranslation?> = _currentTranslation
} 