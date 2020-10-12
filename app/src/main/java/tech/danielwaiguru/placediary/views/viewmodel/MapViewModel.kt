package tech.danielwaiguru.placediary.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.danielwaiguru.placediary.models.Bookmark
import tech.danielwaiguru.placediary.repository.BookmarkRepository

class MapViewModel(private val repository: BookmarkRepository): ViewModel() {
    val allBookmarkedPlaces = repository.allBookmarkedPlaces
    fun addBookmark(bookmark: Bookmark) = viewModelScope.launch {
        repository.addBookmark(bookmark)
    }

}