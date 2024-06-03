package com.example.corts.data.di


import com.example.corts.data.DefaultMapRepository
import com.example.corts.data.MapRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {


    @Singleton
    @Binds
    fun bindsMapRepository(
        mapRepository: DefaultMapRepository
    ): MapRepository // initiate the time repository


}