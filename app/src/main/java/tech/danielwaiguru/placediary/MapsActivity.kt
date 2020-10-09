package tech.danielwaiguru.placediary

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import tech.danielwaiguru.placediary.common.Constants.REQUEST_PERMISSIONS_CODE

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        locationProviderClient()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    private fun locationProviderClient(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }
    private fun getCurrentLocation(){
        val a = hasPermissions()
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            requestPermissions()
        }
        else{
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val currentLocation = it.result
                if (currentLocation != null){
                    val zoomLevel = 16.0f
                    val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    mMap.addMarker(MarkerOptions().position(latLng).title("Your Location"))
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
                    mMap.moveCamera(update)
                }
                else{
                    TODO()
                }
            }
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