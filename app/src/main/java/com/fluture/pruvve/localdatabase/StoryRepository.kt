package com.fluture.pruvve.localdatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryRepository(private val storyDao: StoryDao) {

    suspend fun getStories():List<SavedStory>{
        return storyDao.getStoriesByNames()
    }

    suspend fun upsertStory(story: SavedStory) {
        withContext(Dispatchers.IO) {
            storyDao.upsertStories(story)
        }
    }

    suspend fun deleteStory(story: SavedStory) {
        withContext(Dispatchers.IO) {
            storyDao.deleteStories(story)
        }
    }

    suspend fun upsertStories(posts: List<SavedStory>) {
        withContext(Dispatchers.IO) {
            posts.forEach {
                storyDao.upsertStories(it)
            }
        }
    }

}