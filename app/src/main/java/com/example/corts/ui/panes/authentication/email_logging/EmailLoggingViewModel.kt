package com.example.corts.ui.panes.authentication.email_logging

import androidx.lifecycle.ViewModel
import com.example.corts.data.model.EmailLoggingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EmailLoggingViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailLoggingUiState())

    val uiState: StateFlow<EmailLoggingUiState> = _uiState.asStateFlow()

    fun updateEmail(newEmail: String){
        _uiState.value = _uiState.value.copy(email = newEmail, isValid = checkIfEmailValid(newEmail))
    }

    private fun checkIfEmailValid(email: String): Boolean {
        if(email == ""){
            return true
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        if (!email.matches(emailRegex.toRegex())) {
            return false
        }

        // Check if the email is too long or too short
        if (email.length > 254 || email.length < 5) {
            return false
        }

        // Split the email into local and domain parts
        val parts = email.split("@")
        val localPart = parts[0]
        val domainPart = parts[1]

        // Check for consecutive dots in local part
        if (localPart.contains("..")) {
            return false
        }

        // Add more specific checks as needed

        return true
    }


}
