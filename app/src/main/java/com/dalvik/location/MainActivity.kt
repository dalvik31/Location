package com.dalvik.location


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dalvik.locationmanagement.LocationManagement
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationManager: LocationManagement
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializamos location manager
        inicializateLocationManager()

        //Obtenemos primera ubicacion
        getCurrentLocation()

        //Evento clic de boton para obtener la ultima ubicacion
        buttonLocation.setOnClickListener {
            getCurrentLocation()
        }

        //Evento clic para comenzar con el tracking de la ubicacion
        buttonStart.setOnClickListener {
            startTracking()
        }

        //Evento clic para detener el tracking de la ubicacion
        buttonStop.setOnClickListener {
            locationManager.stopLocationUpdates()
        }


    }

    private fun inicializateLocationManager(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        LocationManagement.from(this)

        locationManager = LocationManagement.from(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mMap = map

    }

    @SuppressLint("NewApi", "MissingPermission")
    private fun getCurrentLocation() {
        locationManager
            .messagePermission("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
            .messageObtainLocation("Obteniendo ubicacion")
            .colorProgress(R.color.purple_200)
            .isLocationTracking(false)
            .getLocation { latlng, error ->

                if (error.isNullOrEmpty()) {
                    mMap.isMyLocationEnabled = true
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng!!, 16.0f))
                    Toast.makeText(
                        this,
                        "${latlng!!.latitude} , ${latlng.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("NewApi", "MissingPermission")
    private fun startTracking(){
        locationManager
            .messagePermission("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
            .messageObtainLocation("Obteniendo ubicacion")
            .colorProgress(R.color.purple_200)
            .isLocationTracking(true)
            .getLocation { latlng, error ->
                if (error.isNullOrEmpty()) {
                    mMap.isMyLocationEnabled = true
                    Toast.makeText(
                        this,
                        "${latlng!!.latitude} , ${latlng.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
    }

}