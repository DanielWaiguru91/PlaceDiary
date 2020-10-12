package tech.danielwaiguru.placediary.adapters

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.custom_marker.view.*
import tech.danielwaiguru.placediary.R
import tech.danielwaiguru.placediary.views.MapsActivity

class InfoWindowAdapter(context: Activity): GoogleMap.InfoWindowAdapter {
    private val customView: View by lazy {
        context.layoutInflater.inflate(R.layout.custom_marker, null)
    }
    init {

    }
    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

    override fun getInfoContents(marker: Marker?): View {
        customView.poiName.text = marker?.title ?: ""
        customView.poiPhone.text = marker?.snippet ?: ""
        customView.poiPhoto.setImageBitmap((marker?.tag as MapsActivity.PlaceInfo).image)
        return  customView
    }
}