package com.example.evgramacharge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evgramacharge.Booking
import com.example.evgramacharge.ChargingPoint
import com.example.evgramacharge.ChargingRepository
import com.example.evgramacharge.distanceKm // 👈 Added this to import your new location helper calculation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EVViewModel(
    private val repo: ChargingRepository = ChargingRepository()
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val uid get() = auth.currentUser?.uid

    val onlinePoints: StateFlow<List<ChargingPoint>> = repo.observeOnlinePoints()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 👇 ADDED FOR REAL-TIME GPS TRACKING & SORTING STACK 👇
    private val userLocation = MutableStateFlow<Pair<Double, Double>?>(null)

    fun updateLocation(lat: Double, lng: Double) {
        userLocation.value = lat to lng
    }

    val nearbyPoints: StateFlow<List<Pair<ChargingPoint, Double?>>> =
        combine(onlinePoints, userLocation) { pts, loc ->
            pts.map { p ->
                p to loc?.let { distanceKm(it.first, it.second, p.latitude, p.longitude) }
            }.sortedBy { it.second ?: Double.MAX_VALUE }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    // 👆 ADDED FOR REAL-TIME GPS TRACKING & SORTING STACK 👆

    val myPoint: StateFlow<ChargingPoint?> = flow {
        val currentUid = uid
        if (currentUid == null) emit(null) else emitAll(repo.observeHostPoint(currentUid))
    }.catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val myBookings: StateFlow<List<Booking>> = flow {
        val currentUid = uid
        if (currentUid == null) emit(emptyList()) else emitAll(repo.observeBookings(currentUid))
    }.catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registerHost(name: String, location: String, socketType: String, price: Double, batteries: Int, lat: Double, lng: Double) = viewModelScope.launch {
        val hostId = uid ?: return@launch
        repo.addPoint(
            ChargingPoint(
                hostId = hostId,
                name = name,
                location = location,
                socketType = socketType,
                pricePerHour = price,
                batteriesAvailable = batteries,
                latitude = lat,
                longitude = lng
            )
        )
    }

    fun toggleHostStatus(point: ChargingPoint) = viewModelScope.launch {
        repo.setOnline(point.id, !point.online)
    }

    fun bookSlot(point: ChargingPoint, date: String, timeSlot: String) = viewModelScope.launch {
        val userId = uid ?: return@launch
        repo.addBooking(
            Booking(
                userId = userId,
                chargingPointId = point.id,
                chargingPointName = point.name,
                date = date,
                timeSlot = timeSlot,
                totalPrice = point.pricePerHour
            )
        )
    }
}
