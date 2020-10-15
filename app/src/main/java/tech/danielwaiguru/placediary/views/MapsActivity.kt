package tech.danielwaiguru.placediary.views

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import tech.danielwaiguru.placediary.R
import tech.danielwaiguru.placediary.adapters.InfoWindowAdapter
import tech.danielwaiguru.placediary.common.Constants.REQUEST_PERMISSIONS_CODE
import tech.danielwaiguru.placediary.models.BookmarkView
import tech.danielwaiguru.placediary.views.viewmodel.MapViewModel
import timber.log.Timber

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val mapViewModel by viewModels<MapViewModel>()
    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    //private var locationRequest: LocationRequest? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        locationProviderClient()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initPlacesClient()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapListeners()
        bookmarkMarkerObserver()
        getCurrentLocation()
    }
    private fun mapListeners(){
        mMap.setInfoWindowAdapter(InfoWindowAdapter(this))
        mMap.setOnPoiClickListener {
            detailPoi(it)
        }
        mMap.setOnInfoWindowClickListener {
            saveMarkerInfo(it)
        }
    }
    private fun initPlacesClient(){
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
    }
    private fun bookmarkMarkerObserver(){
        mapViewModel.getBookmarkMarkerView()?.observe(this, { bookmarkViews ->
            mMap.clear()
            displayAllBookmarkMarkers(bookmarkViews)
        })
    }
    //display detailed info of a poi selected
    private fun detailPoi(pointOfInterest: PointOfInterest){
        val placeId = pointOfInterest.placeId
        val placeDataFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        val request = FetchPlaceRequest
            .builder(placeId, placeDataFields)
            .build()
        placesClient.fetchPlace(request).addOnSuccessListener { placeResponse ->
            val place = placeResponse.place
            getPlacePhoto(place)
        }
            .addOnFailureListener {
                if (it is ApiException){
                    Timber.d(it)
                }
            }
    }
    private fun getPlacePhoto(place: Place){
        val photoMetaData = place.photoMetadatas?.get(0)
        if (photoMetaData == null){
            addPoiMarker(place, null)
            return
        }
        val photoRequest = FetchPhotoRequest
            .builder(photoMetaData)
            .setMaxWidth(resources.getDimensionPixelSize(R.dimen.photo_size_width))
            .setMaxHeight(resources.getDimensionPixelSize(R.dimen.photo_size_height))
            .build()
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { photoResponse ->
            val bitmap = photoResponse.bitmap
            addPoiMarker(place,bitmap)
        }
            .addOnFailureListener {
                if (it is ApiException){
                    Timber.d("it: ${it.statusCode}")
                }
            }
    }
    private fun addPoiMarker(place: Place, photo: Bitmap?){
        /*val placePhoto = if (photo == null){
            BitmapDescriptorFactory.defaultMarker()
        }
        else{
            BitmapDescriptorFactory.fromBitmap(photo)
        }*/
        val marker = mMap.addMarker(MarkerOptions()
            .position(place.latLng as LatLng)
            .title(place.name)
            .snippet(place.phoneNumber)
        )
        marker.tag = PlaceInfo(place, photo)
    }
    private fun locationProviderClient(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }
    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            requestPermissions()
        }
        else{
            /*if (locationRequest == null){
                locationRequest = LocationRequest.create()
                locationRequest?.let {locationRequest ->
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    locationRequest.interval = LOCATION_UPDATES_INTERVAL
                    locationRequest.fastestInterval = FASTEST_LOCATION_UPDATES_INTERVAL
                    val locationCallback = object : LocationCallback(){
                        override fun onLocationResult(locationResult: LocationResult?) {
                            super.onLocationResult(locationResult)
                            getCurrentLocation()
                        }
                    }
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest, locationCallback, null
                    )
                }
            }*/
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val currentLocation = it.result
                if (currentLocation != null){
                    val zoomLevel = 16.0f
                    val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    //mMap.clear()
                    //mMap.addMarker(MarkerOptions().position(latLng).title("Your Location"))
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
                    mMap.moveCamera(update)
                }
                else{
                    Timber.d(getString(R.string.location_error))
                }
            }
        }
    }
    private fun saveMarkerInfo(marker: Marker){
        val placeInfo = marker.tag as PlaceInfo
        if (placeInfo.place != null){
            mapViewModel.createBookmark(placeInfo.place, placeInfo.image)
        }
        marker.remove()
    }
    private fun showTappedPlace(bookmarkView: BookmarkView): Marker? {
        val marker = mMap.addMarker(MarkerOptions()
            .position(bookmarkView.location)
            .icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE
            ))
            .alpha(0.8f)
        )
        marker.tag = bookmarkView
        return marker
    }
    private fun displayAllBookmarkMarkers(bookmarkViews: List<BookmarkView>){
        for (bookmarkView in bookmarkViews){
            showTappedPlace(bookmarkView)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_CODE){
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }
           /* else if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                if(ActivityCompat
                        .shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION)){
                }
            }*/
        }
    }
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_CODE
        )
    }
    private fun hasPermissions() =
        ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    class PlaceInfo(val place: Place? = null, val image: Bitmap? = null)
}