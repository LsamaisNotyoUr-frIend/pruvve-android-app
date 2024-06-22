package com.fluture.pruvve.localdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "savedpost")
data class SavedPost (
    val profilePicUrl: String,
    val timeStamp: String,
    val name: String,
    val title: String,
    val videoUrl: String,
    var follow: Boolean,
    val views: String,
    val comments: String,
    val likes: String,
    val otherUsersId: Int,
    val postId: Int,
    @PrimaryKey(autoGenerate = true)
    val roomId: Int = 0,
)