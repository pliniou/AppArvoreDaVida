package com.example.apparvoredavida.model

import kotlinx.serialization.Serializable

/**
 * Classe que representa um versículo da Bíblia.
 * @param numero Número do versículo
 * @param texto Texto do versículo
 */
@Serializable
data class Versiculo(
    val numero: Int,
    val texto: String
) 