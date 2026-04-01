package com.example.demo.screens

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class VisitorRequest(
    val requestId: String = "",
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val purpose: String = "",
    val personToMeet: String = "",
    val visitDate: String = "",
    val status: String = "PENDING",
    val timestamp: Any? = null,
    val currentZone: String = "",
    val lastZoneUpdate: Any? = null,

    // Government ID proof — used for identity verification across visits
    val idType: String = "",        // "AADHAAR" or "PAN"
    val idNumber: String = "",      // stored as-is, masked only in UI
    val isFlagged: Boolean = false, // true if same ID was submitted with a different name
    val fcmToken: String = ""       // Firebase Cloud Messaging token for push notifications
)

// Alias or duplicate for history usage if needed
typealias VisitorHistory = VisitorRequest

// Supporting old name for compatibility while transitioning if necessary
typealias details = VisitorRequest
