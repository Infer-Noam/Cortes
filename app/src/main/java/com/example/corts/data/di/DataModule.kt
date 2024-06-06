package com.example.corts.data.di


import com.example.corts.data.repository.AccountRepository
import com.example.corts.data.repository.AuthRepository
import com.example.corts.data.repository.DefaultAccountRepository
import com.example.corts.data.repository.DefaultGoogleAuthRepository
import com.example.corts.data.repository.pointRepositories.DefaultFirebasePointRepository
import com.example.corts.data.repository.pointRepositories.DefaultPointRepository
import com.example.corts.data.repository.pointRepositories.FirebasePointRepository
import com.example.corts.data.repository.pointRepositories.PointRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsPointRepository(
        pointRepository: DefaultPointRepository
    ): PointRepository

    @Singleton
    @Binds
    fun bindsFireBasePointRepository(
        firebasePointRepository: DefaultFirebasePointRepository
    ): FirebasePointRepository

    @Singleton
    @Binds
    fun bindsAccountRepository(
        accountRepository: DefaultAccountRepository
    ): AccountRepository

    @Singleton
    @Binds
    fun bindsAuthRepository(
        authRepository: DefaultGoogleAuthRepository
    ): AuthRepository
}