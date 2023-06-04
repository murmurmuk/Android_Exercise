package com.example.android_exercise.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android_exercise.data.db.entity.FavoriteMovie

@Dao
interface FavoriteMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ids: List<FavoriteMovie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ids: FavoriteMovie)
    @Query("SELECT EXISTS (SELECT * FROM favorite_movie_table WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Delete
    suspend fun delete(movie: FavoriteMovie)

    @Query("DELETE FROM favorite_movie_table")
    suspend fun deleteAll()
}