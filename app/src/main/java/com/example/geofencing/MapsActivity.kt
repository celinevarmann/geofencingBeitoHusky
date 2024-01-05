package com.example.geofencing

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.geofencing.databinding.ActivityMapsBinding
import com.example.geofencing.ui.theme.GeofencingTheme
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener {

    private val TAG = "MapsActivity"

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geofenceHelper : GeofenceHelper
    private lateinit var geofencingClient : GeofencingClient

    private var GEOFENCE_RADIUS : Float = 20.0F
    private var GEOFENCE_ID : String = "SOME_GEOFENCE_ID"
    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Fra tutorial:
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val goldenGateBridge = LatLng(37.8201, -122.4783)
        //mMap.addMarker(MarkerOptions().position(goldenGateBridge).title("Marker on Golden Gate Bridge"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(goldenGateBridge))
        enableUserLocation()

        mMap.setOnMapLongClickListener(this)
    }


    private fun enableUserLocation(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We have permission
                mMap.isMyLocationEnabled = true
            } else {
                // We do not have permission
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We have permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show()
            } else {
                // We do not have permission
                Toast.makeText(this, "Background location access is neccessary...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapLongClick(latLng: LatLng) {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val latlngList = arrayOf(LatLng(59.943118, 10.713945), LatLng(59.944248, 10.714118), LatLng(59.943324, 10.713911))
            tryAddingGeofence(latlngList)
        } else {
            // Be om bakgrunnslokasjonsbevilgning hvis det ikke er gitt
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
            )
        }

        //tryAddingGeofence(latlngList)

        //tryAddingGeofence(latLng)
        // PRØVER Å HARDKODE INN KOORDINATER
        /*
        59.944248, 10.714118  - Toppen av bakken mot HSH
        59.943324, 10.713911 - HSH til høyre, hovedveien stopper straks og du skal snart svinge mot venstre
        59.943118, 10.713945 - sving mot venstre her, slak nedoverbakke
        59.943052, 10.715075 - om 50 meter skal du svinge til venstre, senk farten*/


        // Det er bare den siste av disse som lager toast. Kanskje fordi metoden bare tar én LatLng av gangen

        /*val cList = listOf(
            LatLng(59.944248, 10.714118),
            //LatLng(59.943324, 10.713911),
            LatLng(59.943118, 10.713945),
            //LatLng(59.943052, 10.715075)
            )

        for (item in cList){
            tryAddingGeofence(item)
        }*/
    }

    private fun tryAddingGeofence(latlngList: Array<LatLng>){
        //mMap.clear()
        for (latlng in latlngList){
            addMarker(latlng)
            addCircle(latlng, GEOFENCE_RADIUS)
            addGeofence(latlng, GEOFENCE_RADIUS)
        }
    }

    private fun addMarker(latLng : LatLng){
        val markerOptions : MarkerOptions = MarkerOptions().position(latLng)
            .icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)) // fargen på markøren
        mMap.addMarker(markerOptions)
    }
    
    private fun addCircle(latLng: LatLng, radius : Float){
        val circleOptions : CircleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255,168,218,255))
        circleOptions.fillColor(Color.argb(100,168,218,255))
        //circleOptions.strokeWidth()
        mMap.addCircle(circleOptions)
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latLng : LatLng, radius: Float){
        val geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.collectPendingIntent()

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")
            }
            .addOnFailureListener { e ->
                val errorMessage = geofenceHelper.getErrorString(e)
                Log.d(TAG, "onFailure : $errorMessage")
            }
    }

}