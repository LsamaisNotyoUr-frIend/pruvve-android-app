package com.fluture.pruvve.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [SavedPost::class], version = 1)
abstract class PruvveDatabase: RoomDatabase() {
    abstract val postDao: PostDao
}