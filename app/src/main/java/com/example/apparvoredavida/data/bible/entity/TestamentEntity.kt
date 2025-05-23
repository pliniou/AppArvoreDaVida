package com.example.apparvoredavida.data.bible.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "testament")
data class TestamentEntity(
    @PrimaryKey @ColumnInfo(name = "id") val testamentId: Int,
    @ColumnInfo(name = "name") val name: String
) 