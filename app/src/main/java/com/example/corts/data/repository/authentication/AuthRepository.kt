package com.example.corts.data.repository.authentication

import android.content.Context
import com.example.corts.data.repository.authentication.methods.EmailAuthRepository
import com.example.corts.data.repository.authentication.methods.GoogleAuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


interface AuthRepository {

    suspend fun signInWithGoogle(context: Context, stopLoading: () -> Unit) : Boolean

    suspend fun signInWithEmail(email: String, context: Context, stopLoading: () -> Unit) : Boolean

    suspend fun signOutWithGoogle(context: Context)

    suspend fun signOut(context: Context)

    fun observeAuthenticationState(): Flow<Boolean>

}
class DefaultAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleAuthRepository: GoogleAuthRepository,
    private val emailAuthRepository: EmailAuthRepository
) :
    AuthRepository {


    override fun observeAuthenticationState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(user != null).isSuccess // Offer true if user is authenticated, false otherwise
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        // Clean up when the flow is no longer needed
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun signInWithGoogle(context: Context, stopLoading: () -> Unit): Boolean {
        return googleAuthRepository.signIn(context, stopLoading)
    }


    override suspend fun signOutWithGoogle(context: Context) {
        googleAuthRepository.signOut(context)
    }

    override suspend fun signInWithEmail(email: String, context: Context, stopLoading: () -> Unit): Boolean {
        return emailAuthRepository.signIn(email, context, stopLoading)
    }




    override suspend fun signOut(context: Context) {
        val user = Firebase.auth.currentUser
        user?.let {
            for (userInfo in it.providerData) {
                val providerId = userInfo.providerId
                // Check if the provider ID matches any known authentication methods
                when (providerId) {
                    EmailAuthProvider.PROVIDER_ID -> {
                        emailAuthRepository.signOut(context)
                    }
                    GoogleAuthProvider.PROVIDER_ID -> {
                        signOutWithGoogle(context = context)
                    }
                    // Add more cases for other providers like Facebook, Twitter, etc.
                }
            }
        }

    }
}

