package tech.danielwaiguru.placediary.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Bookmark (
    @PrimaryKey
    val id: Int = 0,
    val placeId: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String
)