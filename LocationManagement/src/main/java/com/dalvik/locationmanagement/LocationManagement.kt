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
import java.lang.ref.WeakReference

class LocationManagement private constructor(private val activity: WeakReference<AppCompatActivity>) {

    //Permisos
    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //Inicializacion de Location Request
    private var mLocationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 1000 / 2
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    //Inicializacion de objetos que ocupan Context
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity.get()!!)
    private var progressCustom = ProgressCustom.from(activity.get()!!)

    //Inicializacion de variables
    private var messagePermission: String = String()
    private var messageObtainLocation: String = String()
    private var callback: (Location) -> Unit = {}
    private var colorProgress: Int = 0
    private var colorBackground: Int = 0
    private var colorText: Int = 0
    private var isTracking = false

    //Inicializacion de callback para mandar la ubicacion a la activity
    private lateinit var locationCallback: LocationCallback

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
    fun getLastLocation(callback: (Location) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun stopLocationUpdates() {

        if(locationCallback!=null){
            fusedLocationClient.removeLocationUpdates(locationCallback)
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

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        p0 ?: return

                        for (location in p0.locations) {
                            callback(location)
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )


            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            callback(location)
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


}