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
import com.dalvik.progresscustom.ProgressCustom
import java.lang.ref.WeakReference

class LocationManagement private constructor(private val activity: WeakReference<AppCompatActivity>) :
    LocationListener {
    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private  var  progressCustom = ProgressCustom.from(activity.get()!!)
    private var messagePermission: String = String()
    private var messageObtainLocation: String = String()
    private var callback: (Location) -> Unit = {}
    private lateinit var locationManager: LocationManager
    private var colorProgress: Int = 0
    private var colorBackground: Int = 0
    private var colorText: Int = 0


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

    fun getLocation(callback: (Location) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
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
            locationManager = activity.get()?.getSystemService(LOCATION_SERVICE) as LocationManager
            /*val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                callback(location)
            }else{*/
           showProgress()
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this)
            //}
        }
    }

    override fun onLocationChanged(location: Location) {
        callback(location)
        progressCustom.hideProgress()
        locationManager.removeUpdates(this)
    }

    private fun showProgress(){
        if (colorProgress != 0 ) progressCustom.colorProgress(colorProgress)
        if(colorBackground != 0 ) progressCustom.colorBackground(colorBackground)
        if(colorText != 0) progressCustom.colorText(colorText)
        progressCustom.message(messageObtainLocation)
        progressCustom.showProgress()
    }
}