package com.example.apparvoredavida.data.repository

import com.example.apparvoredavida.model.BibleTranslation
import com.example.apparvoredavida.model.Livro
import com.example.apparvoredavida.model.VersiculoDetails
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de acesso aos dados da Bíblia.
 */
interface BibleRepository {
    /**
     * Obtém a lista de traduções disponíveis.
     */
    suspend fun getTranslations(): List<BibleTranslation>

    /**
     * Obtém a lista de livros para uma tradução específica.
     * @param translationId ID da tradução
     */
    suspend fun getBooksByTranslation(translationId: String): List<Livro>

    /**
     * Obtém os detalhes de um versículo específico.
     * @param translationId ID da tradução
     * @param bookName Nome do livro
     * @param chapter Número do capítulo
     * @param verse Número do versículo
     */
    suspend fun getVerseDetails(
        translationId: String,
        bookName: String,
        chapter: Int,
        verse: Int
    ): VersiculoDetails?

    /**
     * Obtém o texto de um capítulo específico.
     * @param translationId ID da tradução
     * @param bookName Nome do livro
     * @param chapter Número do capítulo
     */
    suspend fun getChapterText(
        translationId: String,
        bookName: String,
        chapter: Int
    ): String?

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