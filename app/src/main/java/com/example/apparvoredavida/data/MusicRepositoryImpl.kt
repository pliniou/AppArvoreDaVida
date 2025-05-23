package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.Music
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor() : MusicRepository {
    override suspend fun getMusicById(musicId: String): Music? {
        // TODO: Implementar lógica real para buscar música pelo ID
        // Por enquanto, retorna null ou um mock
        return null
    }
    // Adicionar outras implementações de funções da interface
} 