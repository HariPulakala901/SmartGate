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


@Composable
fun mainscreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            Text(
                text = "SmartGate Visitor",
                color = Color.White,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Register your visit and wait for approval",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(450.dp))

            GradientButton(
                text = "Visitor's Registration",
                onClick = {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid == null) {
                        navController.navigate("register")
                        return@GradientButton
                    }

                    checkPendingRequest(
                        uid = uid,
                        onPendingFound = {
                            navController.navigate("pending")
                        },
                        onNoPending = {
                            navController.navigate("register")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

//            GradientButton(
//                text = "Staff Access",
//                onClick = { navController.navigate("login") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 40.dp)
//            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Every visit verified.\n" +
                        "Every entry secured.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )

        }
    }
}


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

private fun checkPendingRequest(
    uid: String,
    onPendingFound: () -> Unit,
    onNoPending: () -> Unit
) {
    Log.d("PENDING_CHECK", "Checking pending for uid = $uid")

    val dbRef = FirebaseDatabase.getInstance()
        .getReference("visitorRequests")

    dbRef
        .orderByChild("uid")
        .equalTo(uid)
        .get()
        .addOnSuccessListener { snapshot ->
            Log.d("PENDING_CHECK", "Snapshot exists = ${snapshot.exists()}")
            Log.d("PENDING_CHECK", "Children count = ${snapshot.childrenCount}")

            var hasPending = false

            snapshot.children.forEach { child ->
                val status = child.child("status").value?.toString()
                Log.d("PENDING_CHECK", "Found status = $status")

                if (status == "PENDING") {
                    hasPending = true
                }
            }

            if (hasPending) {
                Log.d("PENDING_CHECK", "Navigating to PENDING screen")
                onPendingFound()
            } else {
                Log.d("PENDING_CHECK", "Navigating to REGISTER screen")
                onNoPending()
            }
        }
        .addOnFailureListener { e ->
            Log.e("PENDING_CHECK", "Firebase read failed", e)

            // 🔥 IMPORTANT FALLBACK
            onNoPending()
        }
}