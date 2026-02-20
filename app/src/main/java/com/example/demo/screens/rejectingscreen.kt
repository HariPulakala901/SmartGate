package com.example.demo.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.demo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun rejectingscreen(navController: NavHostController) {

    val context = LocalContext.current

    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.two)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.7f
    )

    val alpha by rememberInfiniteTransition().animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2D30E3),
                        Color(0xFF8D6AAB),
                        Color(0xFF135DC4)
                    )
                )
            )
    ){
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(200)
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(200)
            ) + fadeOut()
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(340.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Request Not Approved.",
                    color = Color.White.copy(alpha = alpha),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                GradientButton(
                    text = "Submit New Request",
                    onClick = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                            ?: return@GradientButton

                        val dbRef = FirebaseDatabase.getInstance()
                            .getReference("visitorRequests")

                        dbRef
                            .orderByChild("uid")
                            .equalTo(uid)
                            .get()
                            .addOnSuccessListener { snapshot ->

                                // ✅ Find ONLY rejected request
                                val rejectedNode = snapshot.children.firstOrNull { child ->
                                    child.child("status").value?.toString() == "REJECTED"
                                }

                                if (rejectedNode == null) {
                                    // fallback – just go to register
                                    navController.navigate("register") {
                                        popUpTo("rejected") { inclusive = true }
                                    }
                                    return@addOnSuccessListener
                                }

                                moveRejectedToHistory(
                                    requestId = rejectedNode.key!!,
                                    uid = uid
                                ) {
                                    navController.navigate("register") {
                                        popUpTo("rejected") { inclusive = true }
                                    }
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 70.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))

                GradientButton(
                    text = "Back to home",
                    onClick = {
                        navController.navigate("intro") {
                            popUpTo("rejected") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 70.dp)
                )
            }
        }
    }
}



private fun moveRejectedToHistory(
    requestId: String,
    uid: String,
    onComplete: () -> Unit
) {
    val db = FirebaseDatabase.getInstance()
    val activeRef = db.getReference("visitorRequests").child(requestId)
    val historyRef = db.getReference("visitHistory").child(uid).child(requestId)

    activeRef.get().addOnSuccessListener { snapshot ->
        if (!snapshot.exists()) {
            onComplete()
            return@addOnSuccessListener
        }

        historyRef.setValue(snapshot.value)
            .addOnSuccessListener {
                activeRef.removeValue().addOnSuccessListener {
                    onComplete()
                }
            }
            .addOnFailureListener { e ->
                Log.e("REJECT_FLOW", "History write failed", e)
            }
    }
}
