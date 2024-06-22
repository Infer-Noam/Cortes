package com.example.corts.data.repository.authentication.methods

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.corts.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GoogleAuthRepository {

    suspend fun signIn(context: Context, stopLoading: () -> Unit) : Boolean

    suspend fun signOut(context: Context)

}

class DefaultGoogleAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth) :
    GoogleAuthRepository {
    override suspend fun signIn(context: Context, stopLoading: () -> Unit): Boolean {
        return withContext(Dispatchers.Main) {
            val googleSignInClient = createGoogleSignInClient(context)
            val signInIntent = googleSignInClient.signInIntent
            var success = false

            // Create a temporary activity result launcher
            val activityResultLauncher = (context as? ComponentActivity)?.activityResultRegistry?.register(
                "googleSignInKey",
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    success = handleGoogleSignInResult(context ,task, stopLoading)
                } else {
                    success = false
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    stopLoading()
                }
            }

            // Launch the sign-in intent
            activityResultLauncher?.launch(signInIntent)

            // The result will be handled in the callback above
            success // Assume true for now (sign-in initiated successfully)
        }
    }

    override suspend fun signOut(context: Context) {
        val googleSignInClient = createGoogleSignInClient(context)

        // Assuming you have a GoogleSignInClient instance (mGoogleSignInClient)
        googleSignInClient.signOut()
            .addOnCompleteListener(TaskExecutors.MAIN_THREAD) { task ->
                if (task.isSuccessful) {
                    firebaseAuth.signOut()
                    // Redirect the user to the login screen or perform any other necessary actions
                } else {
                    // Handle sign-out failure
                }
            }
        firebaseAuth.signOut()
    }

    private fun handleGoogleSignInResult(context: Context, task: Task<GoogleSignInAccount>, stopLoading: () -> Unit) : Boolean {
        var success = false
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(authCredential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(context, "Signed in successfully", Toast.LENGTH_SHORT).show()
                            success = true
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                            success = false
                        }
                    }
            } else {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                success = false
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            success = false
        }

        stopLoading()
        return success
    }
    }
private fun createGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

