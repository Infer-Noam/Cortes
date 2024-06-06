package com.example.corts.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance("https://cortes-37cad-default-rtdb.europe-west1.firebasedatabase.app")
    /* @Provides
    @Singleton
    @Named("userId")
    fun provideUserId(): String {
        // Replace this with the actual logic to get the user's ID
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    } */


}
