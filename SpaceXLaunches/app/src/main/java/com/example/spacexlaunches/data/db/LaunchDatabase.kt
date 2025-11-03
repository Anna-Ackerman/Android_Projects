package com.example.spacexlaunches.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spacexlaunches.data.model.Launch

@Database(
    entities = [Launch::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LaunchDatabase : RoomDatabase() {

    abstract fun launchDao(): LaunchDao

    companion object {
        @Volatile
        private var INSTANCE: LaunchDatabase? = null

        fun getDatabase(context: Context): LaunchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaunchDatabase::class.java,
                    "spacex_launches.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}