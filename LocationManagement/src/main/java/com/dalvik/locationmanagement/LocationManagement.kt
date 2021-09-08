package com.dalvik.locationmanagement

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

class LocationManagement private constructor(private val activity: WeakReference<AppCompatActivity>) :
    LocationListener {
    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var message: String = String()
    private var callback: (Location) -> Unit = {}
    lateinit var locationManager: LocationManager

    private val permissionCheck =
        activity.get()
            ?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
                sendResultAndCleanUp(grantResults)
            }


    companion object {
        fun from(activity: AppCompatActivity) = LocationManagement(WeakReference(activity))
    }

    fun message(description: String): LocationManagement {
        message = description
        return this
    }

    fun getLocation(callback: (Location) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
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

    private fun shouldShowPermissionRationale(activity: AppCompatActivity) =
        requiredPermissions.any { activity.shouldShowRequestPermissionRationale(it) }

    private fun displayRationale(activity: AppCompatActivity) {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.dialog_permission_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(activity.getString(android.R.string.ok)) { _, _ ->
                requestPermissions()
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        if (grantResults.all { it.value }) {
            locationManager = activity.get()?.getSystemService(LOCATION_SERVICE) as LocationManager
            /*val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                callback(location)
            }else{*/
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this)
            //}
        }
    }

    override fun onLocationChanged(location: Location) {
        callback(location)
        locationManager.removeUpdates(this)
    }

}