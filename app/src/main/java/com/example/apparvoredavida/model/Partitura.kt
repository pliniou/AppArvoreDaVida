package com.example.apparvoredavida.model

import kotlinx.serialization.Serializable

/**
 * Modelo que representa uma partitura musical.
 */
@Serializable
data class Partitura(
    /**
     * Identificador único da partitura.
     */
    val id: String,

    /**
     * Título da partitura.
     */
    val title: String,

    /**
     * Nome do arquivo PDF da partitura.
     */
    val fileName: String,

    /**
     * Compositor da partitura.
     */
    val composer: String? = null,

    /**
     * Ano de composição.
     */
    val year: Int? = null,

    /**
     * Instrumento principal.
     */
    val instrument: String? = null,

    /**
     * Nível de dificuldade (1-5).
     */
    val difficulty: Int? = null,

    /**
     * Descrição ou observações sobre a partitura.
     */
    val description: String? = null
) 