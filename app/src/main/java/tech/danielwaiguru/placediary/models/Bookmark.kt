package tech.danielwaiguru.placediary.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Bookmark (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var placeId: String? = null,
    var name : String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var phone: String = ""
)