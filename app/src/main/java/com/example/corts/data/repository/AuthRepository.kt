package com.example.corts.data.repository

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


interface AuthRepository {

    suspend fun signInWithGoogle(context: Context, stopLoading: () -> Unit) : Boolean

    suspend fun signInWithEmail(email: String, context: Context, stopLoading: () -> Unit) : Boolean

    suspend fun signOutWithGoogle(context: Context)

   // suspend fun sendSignInLinkWithEmail(context: Context, email: String) : Boolean

    suspend fun signOut(context: Context)

    fun observeAuthenticationState(): Flow<Boolean>


}
class DefaultGoogleAuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) :
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



    override suspend fun signOutWithGoogle(context: Context) {
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

    override suspend fun signInWithEmail(email: String, context: Context, stopLoading: () -> Unit): Boolean {
        val signInResult = CompletableDeferred<Boolean>()

        val actionCodeSettings = actionCodeSettings {
            url = "https://cortez.page.link/SignIn"
            handleCodeInApp = true
            setAndroidPackageName(
                "com.example.corts",
                true,
                "12",
            )
        }

        try {
            Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings).await()
            Log.d(TAG, "Email sent.")
        } catch (e: Exception) {
            Log.e(TAG, "Sending email failed with an exception", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            stopLoading()
            return false
        }

        // Create an ActivityResultLauncher for handling the sign-in intent result
        val activityResultLauncher = (context as? ComponentActivity)?.activityResultRegistry?.register(
            "emailSignInKey",
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle successful sign-in
                signInResult.complete(true)
            } else {
                // Handle failed sign-in
                signInResult.complete(false)
                Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
            }
            stopLoading()
        }

        // Create and launch the sign-in intent
        val currentIntent = (context as? Activity)?.intent ?: Intent()
        val signInIntent = createEmailSignInIntent(currentIntent)
        if (signInIntent != null) {
            activityResultLauncher?.launch(signInIntent)
            // Do not return here; wait for the result to be completed
        } else {
            signInResult.complete(false) // Failed to create sign-in intent
        }

        return signInResult.await() // This will suspend until the result is completed
    }





    private fun createEmailSignInIntent(intent: Intent): Intent? {
        val deepLinkUrl = intent.data?.toString() // This is your deep link URL

        // Check if the deep link URL is not null
        return if (deepLinkUrl != null) {
            val action = Intent.ACTION_VIEW
            Intent(action, Uri.parse(deepLinkUrl))
        } else {
            // Handle the case where deepLinkUrl is null
            null
        }
    }

    private suspend fun handleEmailSignInResult(email: String,context: Context, data: Intent?, stopLoading: () -> Unit): Boolean {
        val emailLink = data?.data.toString()
        val completableDeferred = CompletableDeferred<Boolean>()

        if (FirebaseAuth.getInstance().isSignInWithEmailLink(emailLink)) {
       //     val email = retrieveUserEmail() // Implement this method to retrieve the user's email
            val credential = EmailAuthProvider.getCredentialWithLink(email, emailLink)

            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    stopLoading()
                    completableDeferred.complete(true)
                } else {
                    // Handle error case
                    Toast.makeText(context, "Error signing in with email", Toast.LENGTH_SHORT).show()
                    stopLoading()
                    completableDeferred.complete(false)
                }
            }
        } else {
            // The link is not a sign-in link or is expired
            Toast.makeText(context, "Invalid or expired sign-in link", Toast.LENGTH_SHORT).show()
            stopLoading()
            completableDeferred.complete(false)
        }

        return completableDeferred.await()
    }


    override suspend fun signOut(context: Context) {
        val user = Firebase.auth.currentUser
        user?.let {
            for (userInfo in it.providerData) {
                val providerId = userInfo.providerId
                // Check if the provider ID matches any known authentication methods
                when (providerId) {
                    EmailAuthProvider.PROVIDER_ID -> {
                        firebaseAuth.signOut()
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

private fun createGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

