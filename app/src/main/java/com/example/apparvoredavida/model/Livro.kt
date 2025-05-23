package com.example.apparvoredavida.model

import kotlinx.serialization.Serializable

@Serializable
data class Livro(
    val nome: String, // Ex: "Gênesis"
    val abreviacao: String? = null, // Ex: "Gn" (Pode não estar em todos os JSONs, tornar opcional)
    val capitulos: List<Capitulo>
    // Adicione outros campos do livro se existirem no JSON, como "testamento"
)