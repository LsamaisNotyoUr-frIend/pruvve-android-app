package com.fluture.pruvve.localdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SavedPost::class, SavedStory::class], version = 1,  exportSchema = false)
abstract class PruvveDatabase: RoomDatabase() {
    abstract val postDao: PostDao

    abstract val storyDao: StoryDao

    companion object {
        @Volatile
        private var INSTANCE: PruvveDatabase? = null

        fun getDatabase(context: Context): PruvveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PruvveDatabase::class.java,
                    "pruvve_database"
                )
                .fallbackToDestructiveMigration() // Use destructive migration
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}