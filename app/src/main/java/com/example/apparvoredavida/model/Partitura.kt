package com.example.apparvoredavida.model

/**
 * Modelo que representa uma partitura musical.
 * @param id Identificador único da partitura
 * @param title Título da partitura
 * @param pdfPath Caminho do arquivo PDF no assets
 */
data class Partitura(
    val id: String,
    val title: String,
    val pdfPath: String
) 