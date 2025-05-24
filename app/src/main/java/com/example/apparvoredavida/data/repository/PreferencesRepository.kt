package com.example.apparvoredavida.data.repository

import com.example.apparvoredavida.model.TamanhoFonte
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getDarkMode(): Flow<Boolean>
    fun getFontSize(): Flow<TamanhoFonte>
    fun getZoom(): Flow<Float>
    fun getRotation(): Flow<Int>
    
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setFontSize(size: TamanhoFonte)
    suspend fun setZoom(zoom: Float)
    suspend fun setRotation(degrees: Int)
} 