package com.example.apparvoredavida.data.bible.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metadata")
data class TranslationMetadataEntity(
    @PrimaryKey(autoGenerate = false) // Assumindo que 'name' (ex: "ARA") pode ser a chave ou há um ID implícito
    @ColumnInfo(name = "name") val translationNameId: String, // Ex: "ARA", "ACF"
    @ColumnInfo(name = "version") val version: Int?,
    @ColumnInfo(name = "copyright") val copyright: String?,
    @ColumnInfo(name = "permissions") val permissions: String?,
    @ColumnInfo(name = "language_id") val languageId: Int?,
    @ColumnInfo(name = "book_name_language") val bookNameLanguage: String?
    // Adicione um campo para o nome completo da tradução se "name" for apenas a sigla
    // Ex: val fullName: String (você pode mapear isso a partir da sigla 'name')
) 