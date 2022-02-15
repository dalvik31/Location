package com.dalvik.location


import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dalvik.locationmanagement.LocationManagement
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationManager: LocationManagement
    private lateinit var mMap: GoogleMap
    private lateinit var polyline: PolylineOptions

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        LocationManagement.from(this)

        polylineOptions()
        locationManager = LocationManagement.from(this)
        getLastLocation()

        buttonLocation.setOnClickListener {
            var mark = true

            locationManager
                .messagePermission("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
                .messageObtainLocation("Obteniendo ubicacion")
                .colorProgress(R.color.purple_200)
                .isLocationTracking(true)
                .getLastLocation {
                    if(mark){
                        mMap.addMarker(
                            MarkerOptions().position(LatLng(it.latitude, it.longitude))
                                .title("Start Tracking")
                        )
                        mark = false
                    }
                    polyline.add(LatLng(it!!.latitude, it!!.longitude))
                    mMap.addPolyline(polyline)
                }

        }


        buttonLocation2.setOnClickListener {
            getLastLocation( "endTracking")
            locationManager.stopLocationUpdates()
        }


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.isMyLocationEnabled = true
    }

    @SuppressLint("NewApi")
    fun getLastLocation(message: String = "") {
        locationManager
            .messagePermission("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
            .messageObtainLocation("Obteniendo ubicacion")
            .colorProgress(R.color.purple_200)
            .isLocationTracking(false)
            .getLastLocation {
                if (message.isNotEmpty()) {
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(it.latitude, it.longitude))
                            .title(message)
                    )
                }
                val sydney = LatLng(it.latitude, it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f))


            }
    }


    private fun polylineOptions(){
        polyline = PolylineOptions()
            .color(Color.RED)
            .width(5f)
    }
}