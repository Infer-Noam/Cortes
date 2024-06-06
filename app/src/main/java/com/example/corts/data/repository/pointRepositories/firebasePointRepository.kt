package com.example.corts.data.repository.pointRepositories

import com.example.corts.data.local.entity.Point
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface FirebasePointRepository {
    suspend fun insertPointToFirebase(point: Point)
    fun getAllPointsFromFirebase(): Flow<List<Point>>

}
class DefaultFirebasePointRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : FirebasePointRepository {
    private fun pointsReference() =
        firebaseDatabase.getReference("points").child(firebaseAuth.currentUser?.uid ?: "")

    override suspend fun insertPointToFirebase(point: Point) {
        val formattedLongitude = point.longitude.toString().replace(".", "")
        val formattedLatitude = point.latitude.toString().replace(".", "")
        val key = "$formattedLongitude-$formattedLatitude-${point.date}"
        pointsReference().child(key).setValue(point)
    }

    override fun getAllPointsFromFirebase(): Flow<List<Point>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val points = dataSnapshot.children.mapNotNull { it.getValue(Point::class.java) }
                trySend(points).isSuccess
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error here
            }
        }
        pointsReference().addValueEventListener(listener)
        awaitClose { pointsReference().removeEventListener(listener) }
    }
}
