package com.example.apparvoredavida.util

import android.content.Context
import java.io.File
import com.example.apparvoredavida.util.Constants

object AssetManager {
    // Funções para listar arquivos diretamente da pasta assets (mantidas)
    fun listBibleFiles(context: Context): List<String> {
        return context.assets.list(Constants.ASSETS_BIBLE)?.toList() ?: emptyList()
    }

    fun listMusicFiles(context: Context): List<String> {
        return context.assets.list(Constants.ASSETS_MP3)?.toList() ?: emptyList()
    }

    fun listPdfFiles(context: Context): List<String> {
        return context.assets.list(Constants.ASSETS_PDF)?.toList() ?: emptyList()
    }

    fun listFontFiles(context: Context): List<String> {
        return context.assets.list(Constants.ASSETS_FONTS)?.toList() ?: emptyList()
    }
} 