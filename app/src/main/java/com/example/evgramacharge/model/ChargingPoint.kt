package com.example.evgramacharge


data class ChargingPoint(
    val id: String = "",
    val hostId: String = "",
    val name: String = "",
    val location: String = "",
    val socketType: String = "15A",
    val pricePerHour: Double = 0.0,
    val batteriesAvailable: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val online: Boolean = true
)
