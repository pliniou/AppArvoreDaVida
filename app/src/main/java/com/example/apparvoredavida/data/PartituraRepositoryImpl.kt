package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.Partitura
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PartituraRepositoryImpl @Inject constructor() : PartituraRepository {
    override suspend fun getScoreById(scoreId: String): Partitura? {
        // TODO: Implementar lógica real para buscar partitura pelo ID
        // Por enquanto, retorna null ou um mock
        return null
    }
    // Adicionar outras implementações de funções da interface
} 