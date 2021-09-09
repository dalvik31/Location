package com.dalvik.location


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dalvik.locationmanagement.LocationManagement
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val locationManager = LocationManagement.from(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonLocation.setOnClickListener{
            locationManager
                .messagePermission("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
                .messageObtainLocation("Obteniendo ubicacion")
                .colorProgress(R.color.purple_200)
                .getLocation {
                    if(it!=null){
                    Log.e("location","latitude ${it.latitude}, longitude${it.longitude}")
                }

            }
        }

    }
}