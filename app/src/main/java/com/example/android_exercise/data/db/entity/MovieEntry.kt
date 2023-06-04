package com.example.android_exercise.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class MovieEntry (
    @PrimaryKey
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String,
    val isFavorite: Boolean,
    val page: Int
)