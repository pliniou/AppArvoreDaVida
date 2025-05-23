package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.Partitura

interface PartituraRepository {
    // Suspenda função para obter detalhes de uma partitura pelo ID
    suspend fun getScoreById(scoreId: String): Partitura?

    // TODO: Adicionar outras funções relacionadas a partituras, se necessário
} 