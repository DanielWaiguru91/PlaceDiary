package tech.danielwaiguru.placediary.models

import com.google.android.gms.maps.model.LatLng

data class BookmarkView (
    var id: Int? = null,
    var location: LatLng = LatLng(0.0, 0.0)
)