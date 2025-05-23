package com.example.apparvoredavida.model

import com.example.apparvoredavida.model.Music
import com.example.apparvoredavida.model.VersiculoDetails
import com.example.apparvoredavida.model.Hino
import com.example.apparvoredavida.model.Partitura
// You will need to import other models like Verse, Hymn, Score when they are defined

// Exemplo de FavoriteDisplayItem
sealed class FavoriteDisplayItem(open val id: String, open val title: String) {
    data class MusicItem(override val id: String, override val title: String, val artist: String?, val musicObject: Music) : FavoriteDisplayItem(id, title)
    data class VerseItem(override val id: String, override val title: String, val reference: String, val verseObject: VersiculoDetails) : FavoriteDisplayItem(id, title) // Usando Any temporariamente, substitua por sua classe Verse
    data class HymnItem(override val id: String, override val title: String, val hymn: Hino) : FavoriteDisplayItem(id, title)
    data class ScoreItem(override val id: String, override val title: String, val score: Partitura) : FavoriteDisplayItem(id, title)
} 