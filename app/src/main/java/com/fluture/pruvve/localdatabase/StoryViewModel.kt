package com.fluture.pruvve.localdatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    val allStories: LiveData<List<SavedStory>> = repository.allStories.asLiveData()
    val storyCount: LiveData<Int> = repository.getStoryCount().asLiveData()
    val isDatabaseForStoriesEmpty: LiveData<Boolean> = repository.isDatabaseForStoriesEmpty().asLiveData()


    fun upsertStory(story: SavedStory) {
        viewModelScope.launch {
            repository.upsertStory(story)
        }
    }

    fun deleteStory(story: SavedStory) {
        viewModelScope.launch {
            repository.deleteStory(story)
        }
    }

    fun upsertPosts(stories: List<SavedStory>) {
        viewModelScope.launch {
            repository.upsertStories(stories)
        }
    }

    class StoryViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}