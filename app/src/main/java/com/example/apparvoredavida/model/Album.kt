package com.example.apparvoredavida.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String, // Adicionado ID para identificação única
    val title: String,
    val artist: String? = null, // Artista pode ser opcional
    val coverPath: String? = null // Caminho da capa pode ser opcional
) 