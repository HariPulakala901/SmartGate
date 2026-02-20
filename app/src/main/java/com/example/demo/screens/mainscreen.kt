package com.example.demo.screens

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demo.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


//@Composable
//fun mainscreen(navController: NavHostController) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        Image(
//            painter = painterResource(id = R.drawable.img),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(150.dp))
//
//            Text(
//                text = "SmartGate Visitor",
//                color = Color.White,
//                fontSize = 35.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth(),
//                lineHeight = 34.sp
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                text = "Register your visit and wait for approval",
//                color = Color.White.copy(alpha = 0.85f),
//                fontSize = 20.sp,
//                textAlign = TextAlign.Center
//            )
//
//            // This takes all remaining space
//            Spacer(modifier = Modifier.weight(1f))
//
//            GradientButton(
//                text = "Visitor's Registration",
//                onClick = {
//                    val uid = FirebaseAuth.getInstance().currentUser?.uid
//                    if (uid == null) {
//                        navController.navigate("register")
//                        return@GradientButton
//                    }
//
//                    checkRequestStatus(
//                        uid = uid,
//                        onApproved = {
//                            navController.navigate("approved")
//                        },
//                        onPending = {
//                            navController.navigate("pending")
//                        },
//                        onNoRequest = {
//                            navController.navigate("register")
//                        }
//                    )
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 40.dp)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            GradientButton(
//                text = "Visit History",
//                onClick = {
//                    navController.navigate("history")
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 40.dp)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Text(
//                text = "Every visit verified.\n" +
//                        "Every entry secured.",
//                color = Color.White.copy(alpha = 0.9f),
//                fontSize = 20.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(40.dp))
//        }
//    }
//}




@Composable
fun mainscreen(navController: NavHostController) {


    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Background
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔹 TOP SECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SmartGate Visitor",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Register your visit and wait for approval",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }

        // 🔹 BOTTOM SECTION (FIXED POSITION)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            GradientButton(
                text = "Visitor's Registration",
                onClick = {

                    val auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser

                    if (user == null) {
                        // 🔥 wait until auth is ready
                        auth.signInAnonymously()
                            .addOnSuccessListener {
                                checkRequestStatus(
                                    uid = it.user!!.uid,
                                    onApproved = { navController.navigate("approved") },
                                    onPending = { navController.navigate("pending") },
                                    onRejected = { navController.navigate("rejected") },
                                    onNoRequest = { navController.navigate("register") }
                                )
                            }
                        return@GradientButton
                    }

                    checkRequestStatus(
                        uid = user.uid,
                        onApproved = { navController.navigate("approved") },
                        onPending = { navController.navigate("pending") },
                        onRejected = { navController.navigate("rejected") },
                        onNoRequest = { navController.navigate("register") }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            GradientButton(
                text = "Visit History",
                onClick = { navController.navigate("history") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Every visit verified.\nEvery entry secured.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}



//@Composable
//fun mainscreen(navController: NavHostController) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        Image(
//            painter = painterResource(id = R.drawable.img),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//
//        Column(
//            modifier = Modifier.fillMaxSize()
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Spacer(modifier = Modifier.height(150.dp))
//
//            Text(
//                text = "SmartGate Visitor",
//                color = Color.White,
//                fontSize = 35.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth(),
//                lineHeight = 34.sp
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                text = "Register your visit and wait for approval",
//                color = Color.White.copy(alpha = 0.85f),
//                fontSize = 20.sp,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.weight(0.5f))
//
//            GradientButton(
//                text = "Visitor's Registration",
//                onClick = {
//                    val uid = FirebaseAuth.getInstance().currentUser?.uid
//                    if (uid == null) {
//                        navController.navigate("register")
//                        return@GradientButton
//                    }
//
//                    checkPendingRequest(
//                        uid = uid,
//                        onPendingFound = {
//                            navController.navigate("pending")
//                        },
//                        onNoPending = {
//                            navController.navigate("register")
//                        }
//                    )
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 40.dp)
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
////            GradientButton(
////                text = "Staff Access",
////                onClick = { navController.navigate("login") },
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(horizontal = 40.dp)
////            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Text(
//                text = "Every visit verified.\n" +
//                        "Every entry secured.",
//                color = Color.White.copy(alpha = 0.9f),
//                fontSize = 20.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//        }
//    }
//}


//@Composable
//fun GradientButton(
//    text: String,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true
//) {
//    ElevatedButton(
//        onClick = onClick,
//        modifier = modifier.height(54.dp),
//        enabled = enabled,
//        colors = ButtonDefaults.elevatedButtonColors(
//            containerColor = Color.Transparent
//        ),
//        elevation = ButtonDefaults.elevatedButtonElevation(
//            defaultElevation = 10.dp,
//            pressedElevation = 20.dp
//        ),
//        contentPadding = PaddingValues(0.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(
//                            Color(0xFF6EC1FF), // light blue
//                            Color(0xFF5B5FE9)  // blue-purple
//                        )
//                    ),
//                    shape = RoundedCornerShape(50)
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = text,
//                color = Color.White,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//        }
//    }
//}

//@Composable
//fun GradientButton(
//    text: String,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true
//) {
//    Box(
//        modifier = modifier
//            .height(54.dp)
//            .background(
//                brush = Brush.horizontalGradient(
//                    listOf(
//                        Color(0xFF6EC1FF),
//                        Color(0xFF5B5FE9)
//                    )
//                ),
//                shape = RoundedCornerShape(50)
//            )
//            .clickable(
//                enabled = enabled,
//                interactionSource = remember { MutableInteractionSource() },
//                indication = null
//            ) {
//                Log.d("BTN_CLICK", "GradientButton clicked")
//                onClick()
//            },
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text,
//            color = Color.White,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.SemiBold
//        )
//    }
//}


@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (pressed) 20.dp else 10.dp,
        label = "buttonElevation"
    )

    Box(
        modifier = modifier
            .height(54.dp)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(50)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF6EC1FF),
                        Color(0xFF5B5FE9)
                    )
                ),
                shape = RoundedCornerShape(50)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

//private fun checkRequestStatus(
//    uid: String,
//    onApproved: () -> Unit,
//    onPending: () -> Unit,
//    onNoRequest: () -> Unit
//) {
//    Log.d("REQ_CHECK", "Checking request for uid = $uid")
//
//    val dbRef = FirebaseDatabase.getInstance()
//        .getReference("visitorRequests")
//
//    dbRef
//        .orderByChild("uid")
//        .equalTo(uid)
//        .get()
//        .addOnSuccessListener { snapshot ->
//
//            var hasApproved = false
//            var hasPending = false
//
//            snapshot.children.forEach { child ->
//                val status = child.child("status").value?.toString()
//                Log.d("REQ_CHECK", "Found status = $status")
//
//                when (status) {
//                    "APPROVED" -> hasApproved = true
//                    "PENDING" -> hasPending = true
//                }
//            }
//
//            when {
//                hasApproved -> {
//                    Log.d("REQ_CHECK", "Navigating to APPROVED")
//                    onApproved()
//                }
//                hasPending -> {
//                    Log.d("REQ_CHECK", "Navigating to PENDING")
//                    onPending()
//                }
//                else -> {
//                    Log.d("REQ_CHECK", "Navigating to REGISTER")
//                    onNoRequest()
//                }
//            }
//        }
//        .addOnFailureListener { e ->
//            Log.e("REQ_CHECK", "Firebase read failed", e)
//            onNoRequest()
//        }
//}

//private fun checkRequestStatus(
//    uid: String,
//    onApproved: () -> Unit,
//    onPending: () -> Unit,
//    onRejected: () -> Unit,
//    onNoRequest: () -> Unit
//) {
//    val dbRef = FirebaseDatabase.getInstance()
//        .getReference("visitorRequests")
//
//    dbRef
//        .orderByChild("uid")
//        .equalTo(uid)
//        .get()
//        .addOnSuccessListener { snapshot ->
//
//            if (!snapshot.exists()) {
//                onNoRequest()
//                return@addOnSuccessListener
//            }
//
//            // 🔥 find latest request
//            val latestRequest = snapshot.children
//                .mapNotNull { it }
//                .maxByOrNull {
//                    it.child("timestamp").getValue(Long::class.java) ?: 0L
//                }
//
//            val status = latestRequest
//                ?.child("status")
//                ?.getValue(String::class.java)
//
//            when (status) {
//                "APPROVED" -> onApproved()
//                "PENDING" -> onPending()
//                "REJECTED" -> onRejected()
//                else -> onNoRequest()
//            }
//        }
//        .addOnFailureListener {
//            onNoRequest()
//        }
//}

private fun checkRequestStatus(
    uid: String,
    onApproved: () -> Unit,
    onPending: () -> Unit,
    onRejected: () -> Unit,
    onNoRequest: () -> Unit
) {
    Log.d("REQ_CHECK", "START uid=$uid")

    val dbRef = FirebaseDatabase.getInstance()
        .getReference("visitorRequests")

    dbRef
        .orderByChild("uid")
        .equalTo(uid)
        .get()
        .addOnSuccessListener { snapshot ->

            Log.d("REQ_CHECK", "snapshot exists = ${snapshot.exists()}")
            Log.d("REQ_CHECK", "children count = ${snapshot.childrenCount}")

            snapshot.children.forEach {
                Log.d(
                    "REQ_CHECK",
                    "child=${it.key}, status=${it.child("status").value}, time=${it.child("timestamp").value}"
                )
            }

            if (!snapshot.exists()) {
                Log.d("REQ_CHECK", "NO REQUEST → REGISTER")
                onNoRequest()
                return@addOnSuccessListener
            }

            val latestRequest = snapshot.children.maxByOrNull {
                it.child("timestamp").getValue(Long::class.java) ?: 0L
            }

            val status = latestRequest
                ?.child("status")
                ?.getValue(String::class.java)

            Log.d("REQ_CHECK", "LATEST STATUS = $status")

            when (status) {
                "APPROVED" -> onApproved()
                "PENDING" -> onPending()
                "REJECTED" -> onRejected()
                else -> onNoRequest()
            }
        }
        .addOnFailureListener { e ->
            Log.e("REQ_CHECK", "FIREBASE FAILED", e)
            onNoRequest()
        }
}