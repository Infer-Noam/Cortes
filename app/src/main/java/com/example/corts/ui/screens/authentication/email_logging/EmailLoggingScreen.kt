package com.example.corts.ui.screens.authentication.email_logging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.corts.ui.composeables.TopAppBars.TopAppBar
import com.example.corts.ui.screens.authentication.AuthViewModel


@Composable
fun EmailLoggingScreen(navController: NavController){
    val emailLoggingViewModel: EmailLoggingViewModel = hiltViewModel()
    val uiState by emailLoggingViewModel.uiState.collectAsStateWithLifecycle()

    val authViewModel: AuthViewModel = hiltViewModel()

    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = "My Screen", onNavigationIconClick = { navController.popBackStack() }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.9f),
                value = uiState.email,
                onValueChange = { emailLoggingViewModel.updateEmail(it)  },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Email
                        , contentDescription = "Email icon"
                    )
                },
                trailingIcon = { IconButton(
                    onClick = { emailLoggingViewModel.updateEmail("") },
                    content = {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear text field"
                        )
                    })
                }
            )

            Text(text = if(uiState.isValid){""} else{"Please enter a valid email address"})

            Button(onClick = { if((uiState.isValid) && uiState.email != ""){
                authViewModel.signInWithEmail(context,uiState.email) } else{}} ) {
                Text(text = "Send email verification")
            }
        }
    }
}
