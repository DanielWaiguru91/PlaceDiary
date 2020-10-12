package tech.danielwaiguru.placediary.repository

import tech.danielwaiguru.placediary.database.BookmarkDao
import tech.danielwaiguru.placediary.models.Bookmark

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {
    val allBookmarkedPlaces = bookmarkDao.getAllBookmarks()
    suspend fun addBookmark(bookmark: Bookmark) = bookmarkDao.addBookmark(bookmark)
    fun createBookmark(): Bookmark{
        return Bookmark()
    }
}