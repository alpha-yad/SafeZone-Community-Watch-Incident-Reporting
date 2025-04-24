package com.tech.safezone

data class EmergencyAlert(
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: String = "",
    val status: String = "active"
) 