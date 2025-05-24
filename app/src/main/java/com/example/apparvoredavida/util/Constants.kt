package com.example.apparvoredavida.util

/**
 * Constantes utilizadas em todo o aplicativo.
 */
object Constants {
    // Rotas
    const val ROUTE_HOME = "home"
    const val ROUTE_BIBLE = "bible"
    const val ROUTE_HYMNS = "hymns"
    const val ROUTE_SCORES = "scores"
    const val ROUTE_MUSIC = "music"
    const val ROUTE_FAVORITES = "favorites"
    const val ROUTE_SETTINGS = "settings"
    const val ROUTE_PLAYER = "player"
    const val ROUTE_VIEWER = "viewer"
    const val ROUTE_SEARCH = "search"
    const val ROUTE_ALBUM = "album"

    // Extensões de arquivo
    const val EXTENSION_SQLITE = ".sqlite"
    const val EXTENSION_PDF = ".pdf"
    const val EXTENSION_MP3 = ".mp3"
    const val EXTENSION_JSON = ".json"

    // Diretórios de assets (padronizado)
    const val DIR_BIBLE = "bible"
    const val DIR_HINARIO = "hinos"
    const val DIR_PARTITURAS = "partituras"
    const val DIR_MUSICAS = "musicas"
    const val DIR_FONTS = "fonts"

    // Preferências
    const val PREF_THEME = "theme"
    const val PREF_FONT = "font"
    const val PREF_FONT_SIZE = "font_size"
    const val PREF_FAVORITES = "favorites"

    // Valores padrão
    const val DEFAULT_FONT_SIZE = 16
    const val MIN_FONT_SIZE = 12
    const val MAX_FONT_SIZE = 24
    const val DEFAULT_THEME = "light"
    const val DEFAULT_FONT = "regular"

    // Configurações de cache
    const val CACHE_MAX_AGE = 24 * 60 * 60 * 1000L // 24 horas em milissegundos
    const val DISK_CACHE_DIR = "disk_cache"
    const val DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024L // 100MB em bytes
    const val IMAGE_CACHE_DIR = "image_cache"

    // Configurações de arquivos
    const val METADATA_FILE = "metadata.json"
    const val ALBUMS_FILE = "albums.json"

    // Configurações de UI
    const val ANIMATION_DURATION = 300L
    const val DEBOUNCE_DELAY = 300L
    const val RETRY_DELAY = 1000L // 1 segundo
    const val MAX_RETRY_ATTEMPTS = 3

    // Configurações de PDF
    const val PDF_PAGE_SIZE = 1024
    const val PDF_CACHE_DIR = "pdf_cache"

    // Configurações de áudio
    const val AUDIO_CACHE_DIR = "audio_cache"
    const val AUDIO_BUFFER_SIZE = 1024 * 1024 // 1MB

    // Configurações de rede
    const val CONNECTION_TIMEOUT = 30_000L // 30 segundos
    const val READ_TIMEOUT = 30_000L // 30 segundos
    const val WRITE_TIMEOUT = 30_000L // 30 segundos

    // Configurações de banco de dados
    const val DATABASE_NAME = "app_arvore_da_vida.db"
    const val DATABASE_VERSION = 1

    // Configurações de DataStore
    const val DATASTORE_NAME = "app_arvore_da_vida_preferences"

    // Configurações de paginação
    const val PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5
} 