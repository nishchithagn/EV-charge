package com.example.evgramacharge

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChargingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val points = db.collection("chargingPoints")
    private val bookings = db.collection("bookings")

    fun observeOnlinePoints(): Flow<List<ChargingPoint>> = callbackFlow {
        val registration = points.whereEqualTo("online", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dataPoints = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChargingPoint::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(dataPoints)
            }
        awaitClose { registration.remove() }
    }

    fun observeHostPoint(uid: String): Flow<ChargingPoint?> = callbackFlow {
        val registration = points.whereEqualTo("hostId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val doc = snapshot?.documents?.firstOrNull()
                trySend(doc?.toObject(ChargingPoint::class.java)?.copy(id = doc.id))
            }
        awaitClose { registration.remove() }
    }

    fun observeBookings(uid: String): Flow<List<Booking>> = callbackFlow {
        val registration = bookings.whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dataBookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Booking::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(dataBookings)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addPoint(point: ChargingPoint) {
        points.add(point).await()
    }

    suspend fun setOnline(id: String, online: Boolean) {
        points.document(id).update("online", online).await()
    }

    suspend fun addBooking(booking: Booking) {
        bookings.add(booking).await()
    }
}
