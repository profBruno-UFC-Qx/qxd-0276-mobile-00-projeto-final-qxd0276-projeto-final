package com.rgcastrof.trustcam.data.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.util.Log
import androidx.annotation.RequiresPermission

class LocationHandler(context: Context): LocationListener {
    private val appContext = context.applicationContext
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var currentFetchedLocation: Location? = null
        private set

    override fun onLocationChanged(location: Location) {
        val current = currentFetchedLocation
        if (current == null || location.accuracy <= current.accuracy)
            currentFetchedLocation = location

        Log.d("LocationListener", "Location status. Latitude: ${location.latitude}, longitude: ${location.longitude}")
    }

    fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestLocation() {
        val lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        currentFetchedLocation = lastGps ?: lastNetwork

        val providers = locationManager.getProviders(true)
        for (provider in providers) {
            locationManager.requestLocationUpdates(
                provider,
                2000L,
                5f,
                this
            )
        }
    }

    fun stopRequest() {
        locationManager.removeUpdates(this)
        currentFetchedLocation = null
    }

}