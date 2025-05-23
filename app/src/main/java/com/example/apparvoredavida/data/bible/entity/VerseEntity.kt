package com.example.apparvoredavida.data.bible.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "verse",
    foreignKeys = [ForeignKey(
        entity = BookEntity::class,
        parentColumns = ["id"], // Nome da coluna PK na tabela 'book'
        childColumns = ["book_id"], // Nome da coluna FK na tabela 'verse'
        onDelete = ForeignKey.CASCADE // Opcional: define o comportamento ao deletar um livro
    )],
    indices = [Index(value = ["book_id"])]
)
data class VerseEntity(
    @PrimaryKey @ColumnInfo(name = "id") val verseId: Int,
    @ColumnInfo(name = "book_id") val bookId: Int, // Chave estrangeira para BookEntity.bookId
    @ColumnInfo(name = "chapter") val chapter: Int,
    @ColumnInfo(name = "verse") val verseNumber: Int, // Renomeado para evitar conflito com a palavra-chave 'verse'
    @ColumnInfo(name = "text") val text: String
) 