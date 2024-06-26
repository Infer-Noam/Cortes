package com.example.corts.service

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
import com.example.corts.data.repository.pointRepositories.PointRepository
import com.example.corts.ui.panes.map.PointViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


/* responsible for inserting new points to the data bases */
@AndroidEntryPoint
class LocationUpdatesService : Service() {

    @Inject
    lateinit var pointRepository: PointRepository

    private val viewModel: PointViewModel by lazy { PointViewModel( this ,pointRepository) }

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
        val channelId = "location_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Tracking")
            .setContentText("Tracking your location")
            .setSmallIcon(R.drawable.logo_dark)
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
