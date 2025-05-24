package com.example.apparvoredavida.data.repository

import com.example.apparvoredavida.model.BibleTranslation
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de acesso aos dados da Bíblia.
 * Nota: A maior parte da lógica de acesso aos dados foi movida para o BibliaViewModel
 * que gerencia diretamente o DAO dinâmico. Este repositório mantém apenas
 * operações básicas de gerenciamento de tradução.
 */
interface BibleRepository {
    /**
     * Obtém a lista de traduções disponíveis.
     */
    suspend fun getTranslations(): List<BibleTranslation>

    /**
     * Observa a tradução atual selecionada.
     */
    fun observeCurrentTranslation(): Flow<BibleTranslation?>

    /**
     * Atualiza a tradução atual.
     * @param translation Nova tradução selecionada
     */
    suspend fun updateCurrentTranslation(translation: BibleTranslation)
} 