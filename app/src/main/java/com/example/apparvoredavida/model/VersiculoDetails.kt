package com.example.apparvoredavida.model

data class VersiculoDetails(
    val livroNome: String,
    val capituloNumero: Int,
    val versiculoNumero: Int,
    val texto: String,
    val id: String // O ID completo usado para favoritar (ex: "ACF_GN_1_1")
) 