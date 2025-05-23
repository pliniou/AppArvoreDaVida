package com.example.apparvoredavida.model

import kotlinx.serialization.Serializable

@Serializable
data class Versiculo(
    val numero: Int, // Ou String, dependendo do seu JSON
    val texto: String
) 