package tech.danielwaiguru.placediary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.danielwaiguru.placediary.common.Constants.DB_NAME
import tech.danielwaiguru.placediary.models.Bookmark

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
abstract class BookmarkDatabase: RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object{
        @Volatile
        private var INSTANCE: BookmarkDatabase? = null
        fun getInstance(context: Context): BookmarkDatabase{
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BookmarkDatabase::class.java,
                        DB_NAME
                    ).build()
                }
                return instance
            }
        }
    }
}