package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.VersiculoDetails

interface BibleRepository {
    // Suspenda função para obter detalhes de um versículo pelo ID
    suspend fun getVerseById(verseId: String): VersiculoDetails?

    // TODO: Adicionar outras funções relacionadas à Bíblia, como obter livros, capítulos, etc.
} 