package tech.danielwaiguru.placediary

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import tech.danielwaiguru.placediary.common.Constants.REQUEST_PERMISSIONS_CODE
import timber.log.Timber

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
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
        getCurrentLocation()
        mMap.setOnPoiClickListener {
            Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
        }
    }
    private fun initPlacesClient(){
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
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
}