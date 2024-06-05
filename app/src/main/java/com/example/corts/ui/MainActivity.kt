package com.example.corts.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

import com.mapbox.geojson.Point
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.Cortés.ui.Map.Map

import com.example.corts.ui.ViewModels.MapViewModel
import com.example.Cortés.ui.theme.CortésTheme
import com.example.corts.location.LocationUpdatesService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.maps.MapboxExperimental


import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionsManager: PermissionsManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @OptIn(MapboxExperimental::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MapViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            var locationGranted by rememberSaveable { mutableStateOf(PermissionsManager.areLocationPermissionsGranted(this)) }
            var latitude by remember { mutableDoubleStateOf(0.0) }
            var longitude by remember { mutableDoubleStateOf(0.0) }

            val permissionsListener: PermissionsListener = object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    // Handle explanation if needed
                }

                override fun onPermissionResult(granted: Boolean) {
                    locationGranted = granted
                }
            }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            // used to access current location
            val locationRequest = LocationRequest.create().apply {
                interval = 10 * 500 // Update interval in milliseconds (e.g., every 60 seconds)
                fastestInterval = 5 * 500 // Fastest update interval
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }

            if (locationGranted) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsManager = PermissionsManager(permissionsListener)
                    permissionsManager.requestLocationPermissions(this)
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //   return
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

                // starts the background service only if permission is granted
                val intent = Intent(this, LocationUpdatesService::class.java)
                this.startService(intent)

            } else {
                permissionsManager = PermissionsManager(permissionsListener)
                permissionsManager.requestLocationPermissions(this)
            }

            CortésTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (latitude != 0.0 && longitude != 0.0) {
                      CortésApp(coordinatesList = uiState.coordinatesList, longitude = longitude, latitude = latitude)
                    }
                }
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\nwhich brings increased type safety via an {@link ActivityResultContract} and the prebuilt\ncontracts for common intents available in\n{@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\ntesting, and allows receiving results in separate, testable classes independent from your\nactivity. Use\n{@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\nin a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\nhandling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}
