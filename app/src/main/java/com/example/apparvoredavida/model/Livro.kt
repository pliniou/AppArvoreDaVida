package com.example.apparvoredavida.model

/**
 * Classe que representa um livro da Bíblia.
 * @param nome Nome do livro
 * @param abreviacao Abreviação do livro (ex: "Gn")
 * @param capitulos Lista de capítulos do livro
 */
data class Livro(
    val nome: String,
    val abreviacao: String? = null,
    val capitulos: List<Capitulo>
)