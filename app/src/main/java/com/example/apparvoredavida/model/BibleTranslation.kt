package com.example.apparvoredavida.model

/**
 * Classe que representa uma tradução da Bíblia.
 * @param name Nome da tradução
 * @param dbPath Caminho do arquivo SQLite no assets
 */
data class BibleTranslation(
    val name: String,
    val dbPath: String
) 