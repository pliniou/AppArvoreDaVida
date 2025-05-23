package com.example.apparvoredavida.data.bible.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "book"
    // Opcional: foreignKeys se você tiver uma entidade TestamentEntity
    // foreignKeys = [ForeignKey(
    //     entity = TestamentEntity::class,
    //     parentColumns = ["id"],
    //     childColumns = ["testament_reference_id"]
    // )]
)
data class BookEntity(
    @PrimaryKey @ColumnInfo(name = "id") val bookId: Int,
    @ColumnInfo(name = "book_reference_id") val bookReferenceId: Int,
    @ColumnInfo(name = "testament_reference_id") val testamentReferenceId: Int,
    @ColumnInfo(name = "name") val name: String // Nome completo do livro
) 