package com.example.android_exercise.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class PopularRemoteKey(
    @PrimaryKey
    val id: Int,
    val nextKey: Int?)