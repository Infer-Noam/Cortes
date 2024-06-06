package com.example.corts.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.corts.data.local.entity.Point
import kotlinx.coroutines.flow.Flow


@Dao
interface PointDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(point: Point): Long

    @Update
    suspend fun update(point: Point)

    @Transaction
    suspend fun upsert(point: Point) {
        val existingPoint = getPointByCoordinates(point.latitude, point.longitude)
        if (existingPoint == null) {
            // Point doesn't exist, insert it
            insert(point)
        } else {
            // Point already exists, update it
            point.uid = existingPoint.uid // Set the existing ID
            update(point)
        }
    }

    @Query("SELECT * FROM point WHERE latitude = :lat AND longitude = :lng LIMIT 1")
    suspend fun getPointByCoordinates(lat: Double, lng: Double): Point?

    @Query("SELECT * FROM point")
    fun getAllPoints(): Flow<List<Point>>
}
