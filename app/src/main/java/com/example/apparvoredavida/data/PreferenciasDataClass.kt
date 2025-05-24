package com.example.apparvoredavida.data

import com.example.apparvoredavida.model.TemaApp
import com.example.apparvoredavida.model.TamanhoFonte

/**
 * Classe de dados que representa as preferências do usuário.
 * @property tema Tema atual do aplicativo
 * @property fonte Nome da fonte selecionada
 * @property tamanhoFonte Tamanho da fonte selecionado
 */
data class PreferenciasDataClass(
    val tema: TemaApp = TemaApp.SISTEMA,
    val fonte: String = "default",
    val tamanhoFonte: TamanhoFonte = TamanhoFonte.MEDIO
) 