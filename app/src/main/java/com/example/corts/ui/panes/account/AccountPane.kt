package com.example.corts.ui.panes.account

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.corts.ui.panes.authentication.AuthViewModel
import com.example.corts.ui.panes.map.PointViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AccountPane(navController: NavController) {
    // val accountViewModel: AccountViewModel = hiltViewModel()

    val pointViewModel: PointViewModel = hiltViewModel()

    val pointUiState by pointViewModel.uiState.collectAsStateWithLifecycle()

    val authViewModel: AuthViewModel = hiltViewModel()

    val authLoading by authViewModel.loading.collectAsStateWithLifecycle()

    val pointLoading by pointViewModel.loading.collectAsStateWithLifecycle()


    val context = LocalContext.current

    var showDeleteLocationDataDialog by remember { mutableStateOf(false) }



    if (authLoading || pointLoading) {
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
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    authViewModel.signOut(context)
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

            Button(
                onClick = {
                    showDeleteLocationDataDialog = true
                }
            ) {
                Text(text = "Delete location data")
            }

            Button(
                onClick = {
                    pointViewModel.exportDataToFile()
                }
            ) {
                Text(text = "Export location data")
            }

            if (showDeleteLocationDataDialog) {
                Dialog(onDismissRequest = { showDeleteLocationDataDialog = false }) {
                    // Custom shape, background, and layout for the dialog
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 5.dp),
                                text = "You're about to delete your location data. Are you sure?:",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .padding(horizontal = 5.dp)
                            ) {
                                TextButton(
                                    onClick = { showDeleteLocationDataDialog = false },
                                ) {
                                    Text("No", style = MaterialTheme.typography.titleMedium)
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                TextButton(
                                    onClick = {
                                        showDeleteLocationDataDialog = false; pointViewModel.deleteData()
                                    },
                                ) {
                                    Text("Yes", style = MaterialTheme.typography.titleMedium) }
                                }


                        }
                    }
                }
            }
        }
    }
}