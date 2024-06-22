package com.example.corts.ui.panes.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.corts.data.local.entity.Point
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/* responsible for getting the current location */

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    // State for location data (latitude and longitude)
    private val _point = MutableStateFlow(Point())
    val point: StateFlow<Point> = _point

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Update latitude and longitude
                _point.value = Point(-1 ,location.longitude, location.latitude, "")
            }
        }
    }

    fun startLocationUpdates() {
        // Request location updates using fusedLocationClient
        val locationRequest = LocationRequest.create().apply {
            interval = 10 * 1000 // Update interval in milliseconds (e.g., every 10 seconds)
            fastestInterval = 5 * 1000 // Fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted, return
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onCleared() {
        // Clean up resources (e.g., stop location updates)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onCleared()
    }
}
