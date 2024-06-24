package com.fluture.pruvve.localdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertStories(savedStory: SavedStory)

    @Delete
    fun deleteStories(savedStory: SavedStory)

    @Query("SELECT * FROM savedstory ORDER BY name ASC")
    fun getStoriesByNames(): Flow<List<SavedStory>>

    @Query("SELECT COUNT(*) FROM savedstory")
    fun getStoryCount(): Flow<Int>
}