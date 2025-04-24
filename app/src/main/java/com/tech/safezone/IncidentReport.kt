package com.tech.safezone

data class IncidentReport(
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: String = "",
    val imageUrl: String = ""
)


