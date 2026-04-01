package com.example.demo.screens

import com.google.firebase.database.*

data class StatusListener(
    val query: Query,
    val listener: ValueEventListener
)

// FIX: Now consistently uses maxByOrNull on timestamp — same logic as all other screens
fun listenToStatus(
    uid: String,
    onStatusChange: (String) -> Unit
): StatusListener {

    val query = FirebaseDatabase.getInstance()
        .getReference("visitorRequests")
        .orderByChild("uid")
        .equalTo(uid)

    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // FIX: Pick latest request by timestamp — not firstOrNull which is unpredictable
            val latestRequest = snapshot.children.maxByOrNull {
                it.key.toString()
            }

            val status = latestRequest
                ?.child("status")
                ?.getValue(String::class.java)

            status?.let { onStatusChange(it) }
        }

        override fun onCancelled(error: DatabaseError) {
            // No-op — caller handles navigation state
        }
    }

    query.addValueEventListener(listener)

    return StatusListener(query = query, listener = listener)
}