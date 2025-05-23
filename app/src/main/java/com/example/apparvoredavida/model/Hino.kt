package com.example.apparvoredavida.model

data class Hino(
    val id: String,
    val numero: Int,
    val titulo: String,
    val autor: String? = null,
    val letra: String? = null
) 