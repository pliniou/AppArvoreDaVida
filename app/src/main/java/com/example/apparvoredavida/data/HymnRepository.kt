package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.Hino

interface HymnRepository {
    // Suspenda função para obter detalhes de um hino pelo ID
    suspend fun getHymnById(hymnId: String): Hino?

    // TODO: Adicionar outras funções relacionadas a hinos, se necessário
} 