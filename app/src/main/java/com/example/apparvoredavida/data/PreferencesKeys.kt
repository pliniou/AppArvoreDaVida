package com.example.apparvoredavida.data

import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {
    val FAVORITE_MUSIC_IDS = stringSetPreferencesKey("favorite_music_ids")
    val FAVORITE_VERSE_IDS = stringSetPreferencesKey("favorite_verse_ids") // Assumindo que versículos terão IDs únicos
    val FAVORITE_HYMN_IDS = stringSetPreferencesKey("favorite_hymn_ids")   // IDs podem ser os nomes dos arquivos PDF
    val FAVORITE_SCORE_IDS = stringSetPreferencesKey("favorite_score_ids") // IDs podem ser os nomes dos arquivos PDF
} 