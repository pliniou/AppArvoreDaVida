package com.example.apparvoredavida.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.apparvoredavida.model.Favorite
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesRepositoryTest {
    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: FavoritesRepository

    @Before
    fun setup() {
        context = mockk()
        dataStore = mockk()
        repository = FavoritesRepository(context)
    }

    @Test
    fun `addFavorite should add music favorite to DataStore`() = runBlocking {
        // Arrange
        val music = Favorite.Music(
            id = "1",
            title = "Test Music",
            artist = "Test Artist",
            path = "test/path"
        )
        val key = stringPreferencesKey("music_1")
        coEvery { dataStore.edit(any()) } returns mockk()

        // Act
        repository.addFavorite(music)

        // Assert
        coVerify { dataStore.edit(any()) }
    }

    @Test
    fun `removeFavorite should remove favorite from DataStore`() = runBlocking {
        // Arrange
        val music = Favorite.Music(
            id = "1",
            title = "Test Music",
            artist = "Test Artist",
            path = "test/path"
        )
        val key = stringPreferencesKey("music_1")
        coEvery { dataStore.edit(any()) } returns mockk()

        // Act
        repository.removeFavorite(music)

        // Assert
        coVerify { dataStore.edit(any()) }
    }

    @Test
    fun `getFavorites should return empty list when no favorites`() = runBlocking {
        // Arrange
        val emptyPreferences = mockk<Preferences>()
        coEvery { dataStore.data.first() } returns emptyPreferences

        // Act
        val favorites = repository.getFavorites().first()

        // Assert
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun `getMusicFavorites should return only music favorites`() = runBlocking {
        // Arrange
        val music = Favorite.Music(
            id = "1",
            title = "Test Music",
            artist = "Test Artist",
            path = "test/path"
        )
        val verse = Favorite.Verse(
            id = "1",
            book = "Genesis",
            chapter = 1,
            verse = 1,
            text = "Test verse",
            translation = "NVI"
        )
        val preferences = mockk<Preferences>()
        coEvery { dataStore.data.first() } returns preferences

        // Act
        val musicFavorites = repository.getMusicFavorites().first()

        // Assert
        assertTrue(musicFavorites.all { it is Favorite.Music })
    }
} 