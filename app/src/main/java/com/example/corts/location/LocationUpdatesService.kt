package com.example.corts.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.corts.R
import com.example.corts.ui.ViewModels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.corts.data.MapRepository
import com.mapbox.geojson.Point
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class LocationUpdatesService : Service() {

    @Inject
    lateinit var mapRepository: MapRepository

    private val viewModel: MapViewModel by lazy { MapViewModel(mapRepository) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest = LocationRequest.create().apply {
        interval = 10 * 6000 // Update interval in milliseconds (e.g., every 60 seconds)
        fastestInterval = 5 * 6000 // Fastest update interval
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult) {
            val latitude = locationResult.lastLocation?.latitude
            val longitude = locationResult.lastLocation?.longitude
            if (latitude != null && longitude != null) {
                if (latitude != 0.0 && longitude != 0.0) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val currentDateTime = LocalDateTime.now()
                    val formattedDate = currentDateTime.format(formatter)

                    viewModel.insertPoint(Point.fromLngLat(longitude, latitude), date = formattedDate)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Create a notification for the foreground service
        val notification = createNotification()

        // Start the service as a foreground service
        startForeground(1, notification)

        // Request location updates
        startLocationUpdates()
    }

    private fun createNotification(): Notification {
        // Build your notification here
        // Example:
        val channelId = "location_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Tracking")
            .setContentText("Tracking your location")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        // Create the notification channel (if not already created)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permissions here
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
