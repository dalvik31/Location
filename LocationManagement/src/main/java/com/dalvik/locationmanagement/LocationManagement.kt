package com.dalvik.locationmanagement

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dalvik.progresscustom.ProgressCustom
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.lang.ref.WeakReference

class LocationManagement private constructor(private val activity: WeakReference<AppCompatActivity>) {

    //Permisos
    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //Inicializacion de Location Request
    private var mLocationRequest: LocationRequest = LocationRequest.create().apply {
        interval = Constants.INTERVAL_LOCATIONMANAGER
        fastestInterval = Constants.FASTES_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    //Inicializacion de objetos que ocupan Context
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity.get()!!)
    private var progressCustom = ProgressCustom.from(activity.get()!!)

    //Inicializacion de variables
    private var messagePermission: String = String()
    private var messageObtainLocation: String = String()
    private var callback: (LatLng?, String) -> Unit = { l, s ->  }
    private var colorProgress: Int = 0
    private var colorBackground: Int = 0
    private var colorText: Int = 0
    private var isTracking = false

    //Inicializacion de callback para mandar la ubicacion al activity
    private  var locationCallback: LocationCallback   = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            for (location in p0.locations) {
                callback(LatLng(location.latitude,location.longitude),"")
            }
        }
    }

    //Solicitud de permisos
    private val permissionCheck =
        activity.get()
            ?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
                sendResultAndCleanUp(grantResults)
            }


    companion object {
        fun from(activity: AppCompatActivity) = LocationManagement(WeakReference(activity))
    }

    fun messagePermission(messagePermission: String): LocationManagement {
        this.messagePermission = messagePermission
        return this
    }

    fun messageObtainLocation(messageObtainLocation: String): LocationManagement {
        this.messageObtainLocation = messageObtainLocation
        return this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getLocation(callback: (LatLng?, String) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun stopLocationUpdates() {
        locationCallback.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    fun colorProgress(colorProgress: Int): LocationManagement {
        this.colorProgress = colorProgress
        return this
    }

    fun colorBackground(colorBackground: Int): LocationManagement {
        this.colorBackground = colorBackground
        return this
    }

    fun colorText(colorText: Int): LocationManagement {
        this.colorText = colorText
        return this
    }

    fun isLocationTracking(isLocationTracking: Boolean): LocationManagement {
        this.isTracking = isLocationTracking
        return this
    }

    private fun handlePermissionRequest() {
        activity.get()?.let { fragment ->
            when {
                shouldShowPermissionRationale(fragment) -> displayRationale(fragment)
                else -> requestPermissions()
            }
        }
    }

    private fun requestPermissions() {
        permissionCheck?.launch(requiredPermissions.toTypedArray())
    }


    @SuppressLint("NewApi")
    private fun shouldShowPermissionRationale(activity: AppCompatActivity) =
        requiredPermissions.any { activity.shouldShowRequestPermissionRationale(it) }

    private fun displayRationale(activity: AppCompatActivity) {
        progressCustom = ProgressCustom.from(activity)
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.dialog_permission_title))
            .setMessage(messagePermission)
            .setCancelable(false)
            .setPositiveButton(activity.getString(android.R.string.ok)) { _, _ ->
                requestPermissions()
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        if (grantResults.all { it.value }) {

            if (isTracking) {


                fusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )


            } else {
                showProgress()
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        hideProgress()
                        if (location != null) {
                            callback(LatLng(location.latitude,location.longitude),"")
                        }else{
                            callback(null,"No se ha podido obtener tu ubicación")
                        }
                    }

            }
        }
    }

    private fun showProgress() {
        if (colorProgress != 0) progressCustom.colorProgress(colorProgress)
        if (colorBackground != 0) progressCustom.colorBackground(colorBackground)
        if (colorText != 0) progressCustom.colorText(colorText)
        progressCustom.message(messageObtainLocation)
        progressCustom.showProgress()
    }

    private fun hideProgress(){
        progressCustom.let {
            it.hideProgress()
        }
    }


}