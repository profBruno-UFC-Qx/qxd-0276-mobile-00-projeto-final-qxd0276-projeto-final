package com.rgcastrof.trustcam.data.location

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.util.Log

class LocationListener(context: Context): LocationListener {
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