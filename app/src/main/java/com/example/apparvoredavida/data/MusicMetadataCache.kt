package com.example.apparvoredavida.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.apparvoredavida.model.Music
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

private val Context.musicMetadataStore: DataStore<Preferences> by preferencesDataStore(name = "music_metadata")

/**
 * Cache para metadados de músicas usando DataStore.
 * Gerencia a persistência e recuperação de metadados de músicas.
 */
@Singleton
class MusicMetadataCache @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.musicMetadataStore

    /**
     * Salva os metadados de uma música no cache.
     * @param music Música a ser salva
     */
    suspend fun saveMusicMetadata(music: Music) {
        dataStore.edit { preferences ->
            val json = Json.encodeToString(music)
            preferences[stringPreferencesKey(music.id)] = json
        }
    }

    /**
     * Salva uma lista de músicas no cache.
     * @param musics Lista de músicas a ser salva
     */
    suspend fun saveMusicMetadataList(musics: List<Music>) {
        dataStore.edit { preferences ->
            musics.forEach { music ->
                val json = Json.encodeToString(music)
                preferences[stringPreferencesKey(music.id)] = json
            }
        }
    }

    /**
     * Obtém os metadados de uma música específica.
     * @param id ID da música
     * @return Flow que emite os metadados da música ou null se não encontrada
     */
    fun getMusicMetadata(id: String): Flow<Music?> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(id)]?.let { json ->
                try {
                    Json.decodeFromString<Music>(json)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    /**
     * Obtém os metadados de todas as músicas no cache.
     * @return Flow que emite a lista de músicas
     */
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

    /**
     * Limpa todo o cache de metadados.
     */
    suspend fun clearCache() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
} 