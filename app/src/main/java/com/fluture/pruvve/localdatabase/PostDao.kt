package com.fluture.pruvve.localdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Upsert
    fun upsertPosts(savedPost: SavedPost)
    @Delete
    fun deletePosts(savedPost: SavedPost)
    @Query("SELECT * FROM savedpost ORDER BY views ASC")
    fun getPostsByLikes(): Flow<List<SavedPost>>

    @Query("SELECT COUNT(*) FROM savedpost")
    fun getPostCount(): Flow<Int>
}