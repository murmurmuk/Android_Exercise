package com.example.android_exercise.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movie_table")
data class FavoriteMovie (
    @PrimaryKey
    val id: Int)