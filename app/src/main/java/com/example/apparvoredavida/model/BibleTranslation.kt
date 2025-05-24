package com.example.apparvoredavida.model

/**
 * Classe que representa uma tradução da Bíblia.
 * @param name Nome completo da tradução (ex: "Almeida Corrigida Fiel")
 * @param dbPath Caminho do arquivo SQLite no assets (ex: "ACF.sqlite")
 */
data class BibleTranslation(
    val name: String,
    val dbPath: String
) 