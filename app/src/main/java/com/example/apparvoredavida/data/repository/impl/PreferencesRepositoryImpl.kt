package com.example.apparvoredavida.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.apparvoredavida.data.repository.PreferencesRepository
import com.example.apparvoredavida.model.TamanhoFonte
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val FONT_SIZE = intPreferencesKey("font_size")
        val ZOOM = floatPreferencesKey("zoom")
        val ROTATION = intPreferencesKey("rotation")
    }

    override fun getDarkMode(): Flow<Boolean> =
        dataStore.data.map { it[PreferencesKeys.DARK_MODE] ?: false }

    override fun getFontSize(): Flow<TamanhoFonte> =
        dataStore.data.map { 
            TamanhoFonte.values().getOrElse(it[PreferencesKeys.FONT_SIZE] ?: TamanhoFonte.MEDIO.ordinal) { 
                TamanhoFonte.MEDIO 
            }
        }

    override fun getZoom(): Flow<Float> =
        dataStore.data.map { it[PreferencesKeys.ZOOM] ?: 1.0f }

    override fun getRotation(): Flow<Int> =
        dataStore.data.map { it[PreferencesKeys.ROTATION] ?: 0 }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    override suspend fun setFontSize(size: TamanhoFonte) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.FONT_SIZE] = size.ordinal
        }
    }

    override suspend fun setZoom(zoom: Float) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.ZOOM] = zoom
        }
    }

    override suspend fun setRotation(degrees: Int) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.ROTATION] = degrees
        }
    }
} 