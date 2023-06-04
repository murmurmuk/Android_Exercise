package com.example.android_exercise.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android_exercise.data.db.dao.MovieDao
import com.example.android_exercise.data.db.dao.PopularRemoteKeyDao
import com.example.android_exercise.data.db.entity.MovieEntry
import com.example.android_exercise.data.db.entity.PopularRemoteKey

@Database(
    entities = [MovieEntry::class, PopularRemoteKey::class],
    version = 1, exportSchema = false)
abstract class CacheDb : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    abstract fun remoteKeyDao(): PopularRemoteKeyDao

    companion object {
        const val DATABASE_NAME = "exercise.db"
    }
}