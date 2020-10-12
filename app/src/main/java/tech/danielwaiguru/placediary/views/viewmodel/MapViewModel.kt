package tech.danielwaiguru.placediary.views.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.launch
import tech.danielwaiguru.placediary.models.Bookmark
import tech.danielwaiguru.placediary.repository.BookmarkRepository

class MapViewModel(private val repository: BookmarkRepository): ViewModel() {
    val allBookmarkedPlaces = repository.allBookmarkedPlaces
    fun createBookmark(place: Place, image: Bitmap?){
        val bookmark = repository.createBookmark()
        bookmark.placeId = place.id
        bookmark.name = place.name.toString()
        bookmark.address = place.address.toString()
        bookmark.latitude = place.latLng?.latitude ?: 0.0
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.phone = place.phoneNumber.toString()
        viewModelScope.launch {
            repository.addBookmark(bookmark)
        }
    }
}