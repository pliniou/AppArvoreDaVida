package com.example.apparvoredavida.model

/**
 * Classe que representa uma versão da Bíblia (ex: ACF, ARA, NVI).
 * @param name Nome completo da versão (ex: "Almeida Corrigida Fiel")
 * @param dbPath Caminho do arquivo SQLite no assets (ex: "ACF.sqlite")
 */
data class BibleVersion(
    val name: String,
    val dbPath: String
) 