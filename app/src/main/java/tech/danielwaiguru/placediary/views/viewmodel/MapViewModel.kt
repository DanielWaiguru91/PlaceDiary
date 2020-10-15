package tech.danielwaiguru.placediary.views.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.launch
import tech.danielwaiguru.placediary.models.Bookmark
import tech.danielwaiguru.placediary.models.BookmarkView
import tech.danielwaiguru.placediary.repository.BookmarkRepository

class MapViewModel(private val repository: BookmarkRepository): ViewModel() {
    private val allBookmarkedPlaces = repository.allBookmarkedPlaces
    private var bookmarks: LiveData<List<BookmarkView>>? = null
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
    private fun bookmarkToBookmarkView(bookmark: Bookmark): BookmarkView {
        return BookmarkView(
            bookmark.id, LatLng(bookmark.latitude, bookmark.longitude)
        )
    }
    private fun mapBookmarkToView(){
        bookmarks = Transformations.map(allBookmarkedPlaces){ localBookmarks ->
            localBookmarks.map { bookmark ->
                bookmarkToBookmarkView(bookmark)
            }
        }
    }
    private fun getBookmarkMarkerView(): LiveData<List<BookmarkView>>?{
        if (bookmarks == null){
            mapBookmarkToView()
        }
        return bookmarks
    }
}