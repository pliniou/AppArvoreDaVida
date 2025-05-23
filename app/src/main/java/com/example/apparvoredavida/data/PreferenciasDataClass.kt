package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.TemaApp
import com.example.apparvoredavida.util.TamanhoFonte

data class PreferenciasDataClass(
    val tema: TemaApp = TemaApp.SISTEMA,
    val fonte: String = "default",
    val tamanhoFonte: TamanhoFonte = TamanhoFonte.MEDIO
) 