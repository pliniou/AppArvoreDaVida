package com.example.apparvoredavida.model

/**
 * Modelo que representa um hino do hinário.
 * @param id Identificador único do hino
 * @param number Número do hino
 * @param title Título do hino
 * @param author Autor do hino (opcional)
 * @param lyrics Letra do hino (opcional)
 */
data class Hino(
    val id: String,
    val number: Int,
    val title: String,
    val author: String? = null,
    val lyrics: String? = null
) 