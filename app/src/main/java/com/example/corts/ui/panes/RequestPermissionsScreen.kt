package com.example.corts.ui.panes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionsScreen(navController: NavController) {
    // Track if the permission is granted or not
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
 //   val locationViewModel: LocationViewModel = hiltViewModel()

    // Request permission when the composable enters the Composition
    LaunchedEffect(key1 = true) {
        locationPermissionState.launchPermissionRequest()
    }

    // Handle UI based on whether the permission is granted or not
    when {
        locationPermissionState.status.isGranted -> {
     //       locationViewModel.startLocationUpdates()
            navController.navigate("account"){
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
        else -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text(text = "Enable location")
                }
            }
        }
       /* locationPermissionState.status.shouldShowRationale -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text(text = "Enable location")
               }
         }
        }
        !locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale -> {
        } */
    }
}
