package com.tech.safezone

data class ServiceRequest(
    val userId: String = "",
    val serviceType: String = "",
    val description: String = "",
    val status: String = "",
    val timestamp: String = ""
) 