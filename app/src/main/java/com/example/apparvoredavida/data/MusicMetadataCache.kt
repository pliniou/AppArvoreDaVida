package com.example.apparvoredavida.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.apparvoredavida.model.Music
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

private val Context.musicMetadataStore: DataStore<Preferences> by preferencesDataStore(name = "music_metadata")

class MusicMetadataCache(private val context: Context) {
    private val dataStore = context.musicMetadataStore

    suspend fun saveMusicMetadata(music: Music) {
        dataStore.edit { preferences ->
            val json = Json.encodeToString(music)
            preferences[stringPreferencesKey(music.id)] = json
        }
    }

    suspend fun saveMusicMetadataList(musics: List<Music>) {
        dataStore.edit { preferences ->
            musics.forEach { music ->
                val json = Json.encodeToString(music)
                preferences[stringPreferencesKey(music.id)] = json
            }
        }
    }

    fun getMusicMetadata(id: String): Flow<Music?> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(id)]?.let { json ->
                Json.decodeFromString<Music>(json)
            }
        }
    }

    fun getAllMusicMetadata(): Flow<List<Music>> {
        return dataStore.data.map { preferences ->
            preferences.asMap().values.mapNotNull { value ->
                try {
                    Json.decodeFromString<Music>(value.toString())
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    suspend fun clearCache() {
        dataStore.edit { it.clear() }
    }
} 