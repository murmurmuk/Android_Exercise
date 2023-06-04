package com.example.android_exercise.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android_exercise.data.db.entity.MovieEntry

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntry>)

    @Query("SELECT * FROM movie_table")
    fun pagingSource(): PagingSource<Int, MovieEntry>

    @Query("DELETE FROM movie_table")
    suspend fun clearAll()

    @Update
    suspend fun update(movie: MovieEntry)

    @Query("SELECT (SELECT COUNT(*) FROM movie_table) == 0")
    fun isEmpty(): Boolean
}