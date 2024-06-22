package com.example.corts.data.repository.authentication.methods

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface EmailAuthRepository {

    suspend fun signIn(email: String, context: Context, stopLoading: () -> Unit) : Boolean

    suspend fun signOut(context: Context)

}

class DefaultEmailAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : EmailAuthRepository {
    override suspend fun signIn(email: String, context: Context, stopLoading: () -> Unit): Boolean {
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
            firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).await()
            Log.d(TAG, "Email sent.")
            Toast.makeText(context, "Please check your E-Mail", Toast.LENGTH_LONG).show()
            stopLoading()
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

    override suspend fun signOut(context: Context) {
        firebaseAuth.signOut()
    }

}