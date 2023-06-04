package com.example.android_exercise.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android_exercise.data.db.dao.FavoriteMovieDao
import com.example.android_exercise.data.db.dao.MovieDao
import com.example.android_exercise.data.db.dao.PopularRemoteKeyDao
import com.example.android_exercise.data.db.entity.FavoriteMovie
import com.example.android_exercise.data.db.entity.MovieEntry
import com.example.android_exercise.data.db.entity.PopularRemoteKey

@Database(
    entities = [MovieEntry::class, PopularRemoteKey::class, FavoriteMovie::class],
    version = 1, exportSchema = false)
abstract class CacheDb : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    abstract fun remoteKeyDao(): PopularRemoteKeyDao

    abstract fun favoriteMovieDao(): FavoriteMovieDao

    companion object {
        const val DATABASE_NAME = "exercise.db"
    }
}