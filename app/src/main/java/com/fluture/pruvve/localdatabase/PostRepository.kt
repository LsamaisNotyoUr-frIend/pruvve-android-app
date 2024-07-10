package com.fluture.pruvve.localdatabase

import android.content.Context
import com.fluture.pruvve.essentials.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PostRepository(private val postDao: PostDao, private val context: Context) {

    val allPosts = postDao.getPostsByLikes()

    suspend fun upsertPost(post: SavedPost) {
        withContext(Dispatchers.IO) {
            // Download and save media files to local storage
            val videoPath = FileUtils.downloadFile(context, post.videoUrl, "video_${post.postId}.mp4")
            val profilePicPath = FileUtils.downloadFile(context, post.profilePicUrl, "profile_${post.postId}.jpg")

            // Update post with local file paths
            val updatedPost = post.copy(
                videoUrl = videoPath ?: post.videoUrl,
                profilePicUrl = profilePicPath ?: post.profilePicUrl
            )

            postDao.upsertPosts(updatedPost)
        }
    }

    suspend fun deletePost(post: SavedPost) {
        withContext(Dispatchers.IO) {
            postDao.deletePosts(post)
        }
    }

    suspend fun upsertPosts(posts: List<SavedPost>) {
        withContext(Dispatchers.IO) {
            // Delete all old cached files
            FileUtils.deleteAllFiles(context)
            // Ensure no more than 7 posts are saved
            postDao.deleteAllPosts()
            posts.take(7).forEach { post ->
                upsertPost(post)
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