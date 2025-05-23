package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.Music

interface MusicRepository {
    // Suspenda função para obter detalhes de uma música pelo ID
    suspend fun getMusicById(musicId: String): Music?
    
    // Adicionar outras funções relacionadas a música, se necessário
} 