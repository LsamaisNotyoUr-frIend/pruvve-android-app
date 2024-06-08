package com.fluture.pruvve.localdatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PostRepository(private val postDao: PostDao) {

    val allPosts = postDao.getPostsByLikes()

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
}