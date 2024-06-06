package com.example.corts.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.Cortés.ui.Map.Map
import com.example.corts.ui.screens.RequestPermissionsScreen
import com.example.corts.ui.screens.account.AccountScreen
import com.example.corts.ui.screens.authentication.AuthViewModel
import com.example.corts.ui.screens.authentication.LoggingScreen
import com.example.corts.ui.screens.authentication.email_logging.EmailLoggingScreen
import com.example.corts.ui.screens.map.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point


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


    NavHost(navController = navController, startDestination = if(isAuthenticated){if(hasLocationPerm){"screens"}else{"request_permissions"}}else{"logging"}) {
        navigation(startDestination = "account", route = "screens") {
            composable("global") { }
            composable("settings") { }
            composable("account") { AccountScreen(navController) }
            composable("map") {
                Map(
                    navController = navController,
                    currentCoordinates = Point.fromLngLat(
                        locationPointState.value.longitude(),
                        locationPointState.value.latitude()
                    )
                )
            }
        }
        // Nested navigation graph for logging
        navigation(startDestination = "main_logging", route = "logging") {
            composable("main_logging") { LoggingScreen(navController) }
            composable("email_logging") {
                EmailLoggingScreen(navController)
            }
        }
        composable("request_permissions") { RequestPermissionsScreen(navController) }
    }

}
