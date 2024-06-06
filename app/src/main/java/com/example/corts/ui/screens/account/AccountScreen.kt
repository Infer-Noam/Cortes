package com.example.corts.ui.screens.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.corts.ui.screens.authentication.AuthViewModel
import com.example.corts.ui.screens.map.PointViewModel

@Composable
fun AccountScreen(navController: NavController) {
    val accountViewModel: AccountViewModel = hiltViewModel()
    val accountUiState by accountViewModel.uiState.collectAsStateWithLifecycle()

    val pointViewModel: PointViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()

    val loading by authViewModel.loading.collectAsStateWithLifecycle()

    val context = LocalContext.current

    if(loading) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(
                        Alignment.Center
                    ),
                color = Color.Red
            )
        }
    }
    else {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                //authViewModel.startLoading()
                authViewModel.signOut(context)
              //  authViewModel.stopLoading()
                Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()

                navController.popBackStack()
            }
        ) {
            Text(text = "Sign out")
        }

        Button(
            onClick = {
                pointViewModel.syncLDBWithRTDB()
            }
        ) {
            Text(text = "Sync the local data base with the server")
        }
    }}
 }