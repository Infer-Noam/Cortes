package com.example.corts.ui.panes.authentication.email_logging

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun EmailInputDialog(onClose:() -> Unit, onConfirm: (Context, String) -> Unit) {
    val emailLoggingViewModel: EmailLoggingViewModel = hiltViewModel()
    val uiState by emailLoggingViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Dialog(onDismissRequest = { onClose() }) {
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
                    text = "Enter your email:",
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    onValueChange = { emailLoggingViewModel.updateEmail(it) },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    placeholder = { Text("example@gmail.com") },
                    isError = !uiState.isValid && uiState.email != "",
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email, contentDescription = "Email icon"
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { emailLoggingViewModel.updateEmail("") },
                            content = {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear text field"
                                )
                            })
                    }
                )

                if (!uiState.isValid && uiState.email != ""){
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "Please enter a valid email"
                    )
                }

                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp).padding(horizontal = 5.dp)) {
                    TextButton(
                        onClick = { onClose() },
                    ) {
                        Text("Close", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = { if(uiState.isValid && uiState.email != ""){ onConfirm(context ,uiState.email)}; onClose() },
                    ) {
                        Text("Confirm", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

