package com.fluture.pruvve.localdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savedstory")
data class SavedStory(
    val name: String,
    val url: String,
    @PrimaryKey(autoGenerate = true)
    val roomId: Int = 0
)