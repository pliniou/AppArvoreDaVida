package com.example.apparvoredavida.data.repository.impl

import android.content.Context
import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.data.repository.BibleRepository
import com.example.apparvoredavida.model.BibleTranslation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório da Bíblia.
 * Nota: A maior parte da lógica de acesso aos dados foi movida para o BibliaViewModel
 * que gerencia diretamente o DAO dinâmico. Este repositório mantém apenas
 * operações básicas de gerenciamento de tradução.
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

    override fun observeCurrentTranslation(): Flow<BibleTranslation?> = _currentTranslation

    override suspend fun updateCurrentTranslation(translation: BibleTranslation) {
        _currentTranslation.value = translation
    }
} 