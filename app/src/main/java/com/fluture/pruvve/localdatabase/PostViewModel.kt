package com.fluture.pruvve.localdatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val allPosts: LiveData<List<SavedPost>> = repository.allPosts.asLiveData()
    val postCount: LiveData<Int> = repository.getPostCount().asLiveData()
    val isDatabaseEmpty: LiveData<Boolean> = repository.isDatabaseEmpty().asLiveData()
//Posts

    fun upsertPost(post: SavedPost) {
        viewModelScope.launch {
            repository.upsertPost(post)
        }
    }

    fun deletePost(post: SavedPost) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }

    fun upsertPosts(posts: List<SavedPost>) {
        viewModelScope.launch {
            repository.upsertPosts(posts)
        }
    }
}

class PostViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
