package com.example.corts.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.sql.Date


@Entity
data class Point(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0, // each block has a unique id

    val longitude: Double,
    val latitude: Double,
    val date: String

)


@Dao
interface MapDao {

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
