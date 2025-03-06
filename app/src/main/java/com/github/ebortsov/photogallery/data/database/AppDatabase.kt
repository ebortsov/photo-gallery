package com.github.ebortsov.photogallery.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SearchHistory::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSearchHistoryDao(): SearchHistoryDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase = checkNotNull(instance) {
            "AppDatabase must be initialized"
        }

        fun initialize(context: Context) {
            instance = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                APP_DATABASE_NAME
            ).build()
        }
    }
}

const val APP_DATABASE_NAME = "app-database"