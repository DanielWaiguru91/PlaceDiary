package tech.danielwaiguru.placediary.database

import androidx.lifecycle.LiveData
import androidx.room.*
import tech.danielwaiguru.placediary.models.Bookmark

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)
    @Query("SELECT * FROM places")
    fun getAllBookmarks(): LiveData<List<Bookmark>>
    @Query("SELECT * FROM places WHERE id = :bookmarkId")
    suspend fun getBookmark(bookmarkId: Int): Bookmark
    @Query("SELECT * FROM places WHERE id = :bookmarkId")
    suspend fun getBookmarkLiveDate(bookmarkId: Int): LiveData<Bookmark>
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBookmark(bookmarkId: Int)
    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
}