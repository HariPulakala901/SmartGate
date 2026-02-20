package com.yourpackage.data.firebase

import com.google.firebase.database.*


data class StatusListener(
    val query: Query,
    val listener: ValueEventListener
)
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
            val status = snapshot.children.firstOrNull()
                ?.child("status")
                ?.getValue(String::class.java)

            status?.let { onStatusChange(it) }
        }

        override fun onCancelled(error: DatabaseError) {
            // optional: log error
        }
    }

    query.addValueEventListener(listener)

    return StatusListener(
        query = query,
        listener = listener
    )
}