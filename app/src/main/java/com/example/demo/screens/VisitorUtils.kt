package com.example.demo.screens

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.database.FirebaseDatabase

sealed class IdVerificationState {
    object Idle : IdVerificationState()
    object Checking : IdVerificationState()
    object NewVisitor : IdVerificationState()
    object ReturningMatch : IdVerificationState()
    object ProxyDetected : IdVerificationState()
    data class WrongIdType(val registeredIdType: String) : IdVerificationState()
}

fun isValidAadhaar(id: String): Boolean {
    return id.length == 12 && id.all { it.isDigit() } && id[0] != '0' && id[0] != '1'
}

fun isValidPan(id: String): Boolean {
    val regex = Regex("[A-Z]{5}[0-9]{4}[A-Z]{1}")
    return regex.matches(id)
}

fun idTypeLabel(type: String): String {
    return when(type) {
        "AADHAAR" -> "Aadhaar Card"
        "PAN" -> "PAN Card"
        else -> type
    }
}

class AadhaarVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val out = StringBuilder()
        for (i in text.indices) {
            out.append(text[i])
            if (i == 3 || i == 7) out.append("-")
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 12) return offset + 2
                return 14
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                return 12
            }
        }
        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}

fun saveVisitorProfile(idType: String, idNumber: String, name: String, phone: String) {
    val db = FirebaseDatabase.getInstance().getReference("visitorProfiles")
    val profile = mapOf(
        "name" to name,
        "phone" to phone,
        "idType" to idType
    )
    val key = "$idType-$idNumber"
    db.child(key).setValue(profile)
}

fun verifyIdInFirebase(
    idType: String,
    idNumber: String,
    currentName: String,
    onResult: (IdVerificationState, String?, String?, String?) -> Unit
) {
    val db = FirebaseDatabase.getInstance().getReference("visitorProfiles")
    val key = "$idType-$idNumber"
    db.child(key).get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            val registeredName = snapshot.child("name").getValue(String::class.java)
            val registeredPhone = snapshot.child("phone").getValue(String::class.java)
            val registeredIdType = snapshot.child("idType").getValue(String::class.java)

            if (registeredIdType != idType) {
                onResult(IdVerificationState.WrongIdType(registeredIdType ?: ""), null, null, registeredIdType)
            } else if (registeredName?.lowercase() != currentName.lowercase() && currentName.isNotBlank()) {
                onResult(IdVerificationState.ProxyDetected, registeredName, registeredPhone, null)
            } else {
                onResult(IdVerificationState.ReturningMatch, registeredName, registeredPhone, null)
            }
        } else {
            onResult(IdVerificationState.NewVisitor, null, null, null)
        }
    }.addOnFailureListener {
        onResult(IdVerificationState.Idle, null, null, null)
    }
}

fun checkRequestStatus(
    uid: String,
    onApproved: () -> Unit,
    onPending: () -> Unit,
    onRejected: () -> Unit,
    onFlagged: () -> Unit,
    onNoRequest: () -> Unit
) {
    FirebaseDatabase.getInstance().getReference("visitorRequests")
        .orderByChild("uid").equalTo(uid).get()
        .addOnSuccessListener { snapshot ->
            val latest = snapshot.children.maxByOrNull {
                it.child("timestamp").getValue(Long::class.java) ?: 0L
            }
            if (latest == null) {
                onNoRequest()
            } else {
                when (latest.child("status").getValue(String::class.java)) {
                    "APPROVED", "INSIDE", "EXITED" -> onApproved()
                    "PENDING" -> onPending()
                    "REJECTED" -> onRejected()
                    "FLAGGED" -> onFlagged()
                    else -> onNoRequest()
                }
            }
        }.addOnFailureListener {
            onNoRequest()
        }
}
