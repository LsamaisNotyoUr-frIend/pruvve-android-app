package com.fluture.pruvve.localdatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PostRepository(private val postDao: PostDao) {

    val allPosts = postDao.getPostsByLikes()
//    posts
    suspend fun upsertPost(post: SavedPost) {
        withContext(Dispatchers.IO) {
            postDao.upsertPosts(post)
        }
    }
    suspend fun deletePost(post: SavedPost) {
        withContext(Dispatchers.IO) {
            postDao.deletePosts(post)
        }
    }
    suspend fun upsertPosts(posts: List<SavedPost>) {
        withContext(Dispatchers.IO) {
            posts.forEach {
                postDao.upsertPosts(it)
            }
        }
    }
    fun isDatabaseEmpty(): Flow<Boolean> {
        return postDao.getPostCount().map { it == 0 }
    }
    fun getPostCount(): Flow<Int> {
        return postDao.getPostCount()
    }

//    Stories
}

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