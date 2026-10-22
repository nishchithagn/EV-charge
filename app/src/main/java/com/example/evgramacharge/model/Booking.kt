package com.example.evgramacharge

data class Booking(
    val id: String = "",
    val userId: String = "",
    val chargingPointId: String = "",
    val chargingPointName: String = "",
    val date: String = "",
    val timeSlot: String = "",
    val totalPrice: Double = 0.0
)
