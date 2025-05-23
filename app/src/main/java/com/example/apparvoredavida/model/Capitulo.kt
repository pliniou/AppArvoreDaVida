package com.example.apparvoredavida.model

import kotlinx.serialization.Serializable

@Serializable
data class Capitulo(
    val numero: Int, // Ou String
    val versiculos: List<Versiculo>
) 