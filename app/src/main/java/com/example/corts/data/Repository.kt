package com.example.corts.data


import com.example.corts.data.local.database.MapDao
import com.example.corts.data.local.database.Point
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface MapRepository {

    suspend fun insertPoint(point: Point)

    fun getAllPoints(): Flow<List<Point>>


}

class DefaultMapRepository @Inject constructor(
    private val mapDao: MapDao
) : MapRepository {


    override suspend fun insertPoint(point: Point) {
        mapDao.upsert(point)
    }

    override fun getAllPoints(): Flow<List<Point>> =
        mapDao.getAllPoints()


}
