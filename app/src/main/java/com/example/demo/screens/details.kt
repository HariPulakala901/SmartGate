package com.example.demo.screens

data class details(
    val requestId: String = "",
    val uid: String  = "",
    val name: String = "",
    val phone: String = "",
    val purpose: String = "",
    val personToMeet: String = "",
    val visitDate: String = "",
    val status: String = "PENDING",
    val timestamp: Any? = null,
    val currentZone: String = "",
    val lastZoneUpdate: Any? = null
)
