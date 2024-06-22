package com.example.corts.data.repository.pointRepositories

import com.example.corts.data.local.dao.PointDao
import com.example.corts.data.local.entity.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


interface PointRepository {

    suspend fun insertPoint(point: Point)

    suspend fun insertPointsToFirebase(points: List<Point>, onCompletion: () -> Unit)

    fun getAllPoints(): Flow<List<Point>>

    suspend fun deleteAllPoints() : Boolean

}

class DefaultPointRepository @Inject constructor(
    private val pointDao: PointDao,
    private val firebasePointRepository: FirebasePointRepository // Inject the Firebase repository here
) : PointRepository {


    override suspend fun insertPoint(point: Point) {
        pointDao.upsert(point) // Insert the point to the local database
        firebasePointRepository.insertPointToFirebase(point) // Also insert the point to Firebase
    }

    override suspend fun insertPointsToFirebase(points: List<Point>, onCompletion: () -> Unit) {
        points.forEach {
            firebasePointRepository.insertPointToFirebase(it) // Also insert the point to Firebase
        }
        onCompletion() // Call the completion handler when all points have been inserted
    }


    override fun getAllPoints(): Flow<List<Point>> =
        combine(
            pointDao.getAllPoints(), // Get points from the local database
            firebasePointRepository.getAllPointsFromFirebase() // Get points from Firebase
                .onStart { emit(emptyList()) } // Emit an initial empty list
        ) { localPoints, firebasePoints ->
            // Merge the two lists of points
            localPoints + firebasePoints
        }

    override suspend fun deleteAllPoints() : Boolean{
        pointDao.deleteAll()
        return firebasePointRepository.deleteAllPointsFromFirebase()
    }
}