package com.example.apparvoredavida.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "pdf_preferences")

@Singleton
class PdfPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    suspend fun saveLastViewedPage(pdfName: String, pageIndex: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey("${pdfName}_last_page")] = pageIndex
        }
    }

    fun getLastViewedPage(pdfName: String): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[intPreferencesKey("${pdfName}_last_page")] ?: 0
        }
    }

    suspend fun saveZoomLevel(pdfName: String, zoomLevel: Float) {
        dataStore.edit { preferences ->
            preferences[floatPreferencesKey("${pdfName}_zoom")] = zoomLevel
        }
    }

    fun getZoomLevel(pdfName: String): Flow<Float> {
        return dataStore.data.map { preferences ->
            preferences[floatPreferencesKey("${pdfName}_zoom")] ?: 1f
        }
    }
} 