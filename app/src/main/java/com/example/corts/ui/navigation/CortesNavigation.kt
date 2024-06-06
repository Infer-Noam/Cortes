package com.example.corts.ui.navigation

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.Cortés.ui.panes.Map.Map
import com.example.corts.service.LocationUpdatesService
import com.example.corts.ui.panes.RequestPermissionsScreen
import com.example.corts.ui.panes.account.AccountPane
import com.example.corts.ui.panes.authentication.AuthViewModel
import com.example.corts.ui.panes.authentication.LoggingScreen
import com.example.corts.ui.panes.map.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Navigation() {

    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.authenticationState.collectAsState(initial = false)
    val navController = rememberNavController()
    val locationViewModel: LocationViewModel = hiltViewModel()
    val locationPointState = locationViewModel.point.collectAsStateWithLifecycle()
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val hasLocationPerm = locationPermissionState.status.isGranted


    if(locationPermissionState.status.isGranted){
        locationViewModel.startLocationUpdates() //starts tracking current location

        val serviceIntent = Intent(LocalContext.current, LocationUpdatesService::class.java) // starts inserting points in the background
        LocalContext.current.startService(serviceIntent)

    }

    NavHost(navController = navController, startDestination = if(isAuthenticated){if(hasLocationPerm){"screens"}else{"request_permissions"}}else{"logging"}) {
        navigation(startDestination = "account", route = "screens") {
            composable("global") { }
            composable("settings") { }
            composable("account") { AccountPane(navController) }
            composable("map") {
               Map(
                    navController = navController,
                    currentCoordinates = locationPointState.value
                )
            }
        }
        composable("logging"){ LoggingScreen() }
        composable("request_permissions") { RequestPermissionsScreen(navController) }
    }

}

