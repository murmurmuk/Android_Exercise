package com.example.android_exercise.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android_exercise.data.db.entity.PopularRemoteKey

@Dao
interface PopularRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: PopularRemoteKey)

    @Query("SELECT * FROM remote_keys")
    suspend fun remoteKey(): PopularRemoteKey

    @Query("DELETE FROM remote_keys")
    suspend fun delete()
}