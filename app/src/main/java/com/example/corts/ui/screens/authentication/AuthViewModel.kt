package com.example.corts.ui.screens.authentication

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.corts.data.repository.AuthRepository
import com.example.corts.ui.animations.loading.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository // Inject your repository here
) : LoadingViewModel() {

    val authenticationState: Flow<Boolean> = authRepository.observeAuthenticationState()

    fun signInWithGoogle(context: Context): Boolean {
        var success = false // Initialize success flag

        try {
            viewModelScope.launch {
                // Call your authentication repository's signIn method
                startLoading()
                success = authRepository.signInWithGoogle(context, stopLoading = { stopLoading() })
             //   stopLoading()
            }
            // Handle any other necessary logic here (e.g., redirecting the user)

        } catch (e: Exception) {
            success = false
        }


        return success
    }

    fun signInWithEmail(context: Context, email: String): Boolean {
        var success = false // Initialize success flag

        try {
            viewModelScope.launch {
                // Call your authentication repository's signIn method
                startLoading()
                success = authRepository.signInWithEmail(context = context, email = email, stopLoading = { stopLoading() })
              //  stopLoading()
            }
            // Handle any other necessary logic here (e.g., redirecting the user)

        } catch (e: Exception) {
            success = false
        }


        return success
    }



    fun signOut(context: Context) {
        viewModelScope.launch {
            startLoading()
            authRepository.signOut(context)
            stopLoading()
            // Handle sign-out (e.g., navigate to login screen)
        }
    }
}
