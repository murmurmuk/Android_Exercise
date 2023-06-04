package com.example.android_exercise.di

import android.content.Context
import androidx.room.Room
import com.example.android_exercise.data.db.CacheDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    fun provideDb(@ApplicationContext context: Context): CacheDb {
        return Room.databaseBuilder(context,
            CacheDb::class.java, CacheDb.DATABASE_NAME
        ).build()
    }
}