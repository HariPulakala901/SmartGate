package com.example.demo.screens

import android.R.attr.visible
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.demo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


//@Composable
//fun pendingscreen(navController: NavHostController) {
//
//    val context = LocalContext.current
//
//    var visible by remember { mutableStateOf(true) }
//
//    val composition by rememberLottieComposition(
//        LottieCompositionSpec.RawRes(R.raw.one)
//    )
//
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations = LottieConstants.IterateForever,
//        speed = 0.7f
//    )
//
//
//    val alpha by rememberInfiniteTransition().animateFloat(
//        initialValue = 0.4f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1200),
//            repeatMode = RepeatMode.Reverse
//        )
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    listOf(
//                        Color(0xFF2D30E3),
//                        Color(0xFF8D6AAB),
//                        Color(0xFF135DC4)
//                    )
//                )
//            )
//    ){
//        AnimatedVisibility(
//            visible = visible,
//            enter = slideInHorizontally(
//                initialOffsetX = { fullWidth -> fullWidth }, // 👈 from right
//                animationSpec = tween(200)
//            ) + fadeIn(animationSpec = tween(200)),
//
//            exit = slideOutHorizontally(
//                targetOffsetX = { fullWidth -> fullWidth }, // 👈 to right
//                animationSpec = tween(200)
//            ) + fadeOut(animationSpec = tween(200))
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    listOf(
//                        Color(0xFF2D30E3),
//                        Color(0xFF8D6AAB),
//                        Color(0xFF135DC4)
//                    )
//                )
//            ),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Box(
//            modifier = Modifier
//                .padding(24.dp)
//                .background(
//                    Color.White.copy(alpha = 0.12f),
//                    RoundedCornerShape(20.dp)
//                )
//                .padding(vertical = 32.dp, horizontal = 24.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//
//                LottieAnimation(
//                    composition = composition,
//                    progress = { progress },
//                    modifier = Modifier.size(240.dp)
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "Request Submitted",
//                    color = Color.White,
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = "Waiting for admin approval...",
//                    color = Color.White.copy(alpha = alpha),
//                    fontSize = 16.sp
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//            }
//        }
//        GradientButton(
//            text = "Update Request",
//            onClick = {
//                navController.navigate("register?edit=true")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 70.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        GradientButton(
//            text = "Delete Request",
//            onClick = {
//                deleteVisitorRequest(
//                    navController = navController,
//                    context = context
//                )
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 70.dp)
//        )
//
//    }
//}
//
//
////private fun deleteVisitorRequest(
////    navController: NavHostController
////) {
////    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
////        ?: return
////
////    val dbRef = com.google.firebase.database.FirebaseDatabase.getInstance()
////        .getReference("visitorRequests")
////
////    dbRef
////        .orderByChild("uid")
////        .equalTo(uid)
////        .get()
////        .addOnSuccessListener { snapshot ->
////            snapshot.children.firstOrNull()?.ref?.removeValue()
////                ?.addOnSuccessListener {
////                    navController.navigate("intro") {
////                        popUpTo("pending") { inclusive = true }
////                    }
////                }
////        }
////}
private fun deleteVisitorRequest(
    navController: NavHostController,
    context: android.content.Context
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val dbRef = FirebaseDatabase.getInstance()
        .getReference("visitorRequests")

    dbRef
        .orderByChild("uid")
        .equalTo(uid)
        .get()
        .addOnSuccessListener { snapshot ->
            snapshot.children.firstOrNull()?.ref?.removeValue()
                ?.addOnSuccessListener {

                    Toast.makeText(
                        context,
                        "Request deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate("intro") {
                        popUpTo("pending") { inclusive = true }
                    }
                }
        }
}





@Composable
fun pendingscreen(navController: NavHostController) {

    val context = LocalContext.current

    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.one)
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

    // ✅ BACKGROUND ALWAYS PRESENT
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
    ) {
        // ✅ ONLY CONTENT IS ANIMATED
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, // 👈 from right
                animationSpec = tween(200)
            ) + fadeIn(animationSpec = tween(200)),

            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }, // 👈 to right
                animationSpec = tween(200)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .background(
                            Color.White.copy(alpha = 0.12f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(240.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Request Submitted",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Waiting for admin approval...",
                            color = Color.White.copy(alpha = alpha),
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GradientButton(
                    text = "Update Request",
                    onClick = {
                        navController.navigate("register?edit=true")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 70.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                GradientButton(
                    text = "Delete Request",
                    onClick = {
                        deleteVisitorRequest(navController, context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 70.dp)
                )
            }
        }
    }
}