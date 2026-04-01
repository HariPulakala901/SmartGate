package com.example.demo.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.demo.R
import com.example.demo.ui.components.GlassButton
import com.example.demo.ui.components.GlassButtonStyle
import com.example.demo.ui.components.GlossyDivider
import com.example.demo.ui.components.LiquidGlassBackground
import com.example.demo.ui.components.LiquidGlassCard
import com.example.demo.ui.theme.SemanticRed
import com.example.demo.ui.theme.BgDeep
import androidx.compose.foundation.layout.Box
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun Rejectingscreen(navController: NavHostController) {

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

//    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.two))
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations  = LottieConstants.IterateForever,
//        speed       = 0.7f
//    )

    val bgColor = BgDeep

    Box(modifier = Modifier
        .fillMaxSize()
        .background(bgColor)
    ) {
        // Hero Image — top 55%, gradient fades into bg
        Image(
            painter = painterResource(id = R.drawable.rejected_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, bgColor),
                            startY = size.height * 0.4f,
                            endY   = size.height
                        )
                    )
                }
        )

        CrimsonAlertBackground()

        Box(modifier = Modifier.fillMaxSize()) {
            LiquidGlassBackground(
                modifier = Modifier.fillMaxSize(),
                backgroundColor = Color.Transparent
            ) {

            AnimatedVisibility(
                visible = visible,
            enter   = slideInHorizontally { it } + fadeIn(),
            exit    = slideOutHorizontally { it } + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement   = Arrangement.Center,
                horizontalAlignment   = Alignment.CenterHorizontally
            ) {
                // Push content down so image is visible above and text doesn't overlap
                Spacer(Modifier.height(390.dp))
                Text(
                    text       = "Request Rejected",
                    color      = SemanticRed,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text      = "Your visit request was not approved",
                    color     = TextSecondary,
                    fontSize  = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

//                LottieAnimation(
//                    composition = composition,
//                    progress    = { progress },
//                    modifier    = Modifier.size(240.dp)
//                )
//
//                Spacer(Modifier.height(8.dp))

                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text       = "What can you do?",
                            color      = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        GlossyDivider()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text      = "You can submit a new visit request with updated details, or return to the home screen.",
                            color     = TextSecondary,
                            fontSize  = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                GlassButton(
                    text    = "Submit New Request",
                    style   = GlassButtonStyle.Primary,
                    onClick = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@GlassButton
                        val dbRef = FirebaseDatabase.getInstance().getReference("visitorRequests")
                        dbRef.orderByChild("uid").equalTo(uid).get()
                            .addOnSuccessListener { snapshot ->
                                val rejectedNode = snapshot.children.firstOrNull { child ->
                                    child.child("status").value?.toString() == "REJECTED"
                                }
                                if (rejectedNode == null) {
                                    navController.navigate("register") {
                                        popUpTo("rejected") { inclusive = true }
                                    }
                                    return@addOnSuccessListener
                                }
                                moveRejectedToHistoryLG(rejectedNode.key!!, uid) {
                                    navController.navigate("register") {
                                        popUpTo("rejected") { inclusive = true }
                                    }
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                GlassButton(
                    text    = "Back to Home",
                    style   = GlassButtonStyle.Secondary,
                    onClick = {
                        navController.navigate("intro") {
                            popUpTo("rejected") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    }
    }
}

@Composable
fun CrimsonAlertBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "crimsonAlert")
    
    val coreScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coreScale"
    )
    
    val coreAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coreAlpha"
    )

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ),
        label = "time"
    )

    val particles = remember {
        List(20) {
            Triple(
                Math.random().toFloat(),
                (Math.random() * 2f + 1f).toFloat(),
                Math.random().toFloat() * 1.5f + 0.5f
            ) to Math.random().toFloat()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val crimson = Color(0xFFFF1A33)
        
        val center = Offset(width / 2f, height * 0.45f)
        val radius = width * 1.1f * coreScale
        
        // Deep breathing red core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    crimson.copy(alpha = coreAlpha),
                    crimson.copy(alpha = coreAlpha * 0.3f),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            center = center,
            radius = radius
        )
        
        // Gentle upward drifting embers
        particles.forEach { (props, phase) ->
            val (relX, pSize, speed) = props
            val x = relX * width
            
            val rawY = phase + (time * speed)
            val wrappedY = 1f - (rawY % 1f)
            val y = wrappedY * height
            
            val alpha = when {
                wrappedY > 0.8f -> (1f - wrappedY) / 0.2f
                wrappedY < 0.2f -> wrappedY / 0.2f
                else -> 1f
            }
            
            if (alpha > 0f) {
                drawCircle(
                    color = crimson.copy(alpha = alpha * 0.8f),
                    radius = pSize,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private fun moveRejectedToHistoryLG(requestId: String, uid: String, onComplete: () -> Unit) {
    val db        = FirebaseDatabase.getInstance()
    val activeRef = db.getReference("visitorRequests").child(requestId)
    val histRef   = db.getReference("visitHistory").child(uid).child(requestId)

    activeRef.get().addOnSuccessListener { snapshot ->
        if (!snapshot.exists()) { onComplete(); return@addOnSuccessListener }
        histRef.setValue(snapshot.value)
            .addOnSuccessListener { activeRef.removeValue().addOnSuccessListener { onComplete() } }
            .addOnFailureListener { e -> Log.e("REJECT_FLOW", "History write failed", e); onComplete() }
    }
}
