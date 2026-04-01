package com.example.demo.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.example.demo.ui.components.LiveDot
import com.example.demo.ui.components.SectionLabel
import com.example.demo.ui.theme.AccentBlue
import com.example.demo.ui.theme.AccentIndigo
import com.example.demo.ui.theme.BgDeep
import com.example.demo.ui.theme.SemanticAmber
import com.example.demo.ui.theme.SemanticRed
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun Pendingscreen(navController: NavHostController) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val uid     = FirebaseAuth.getInstance().currentUser?.uid
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Real-time status redirect
    DisposableEffect(uid) {
        if (uid == null) return@DisposableEffect onDispose {}
        val query = FirebaseDatabase.getInstance()
            .getReference("visitorRequests")
            .orderByChild("uid").equalTo(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latest = snapshot.children.maxByOrNull {
                    it.key.toString()
                }
                when (latest?.child("status")?.getValue(String::class.java)) {
                    "APPROVED" -> navController.navigate("approved") { popUpTo("pending") { inclusive = true } }
                    "REJECTED" -> navController.navigate("rejected") { popUpTo("pending") { inclusive = true } }
                    "FLAGGED"  -> navController.navigate("flagged")  { popUpTo("pending") { inclusive = true } }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        query.addValueEventListener(listener)
        onDispose { query.removeEventListener(listener) }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.one))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations  = LottieConstants.IterateForever,
        speed       = 0.7f
    )

    // Animated track fill 0→60%
    val trackProgress by rememberInfiniteTransition(label = "track").animateFloat(
        initialValue  = 0.2f,
        targetValue   = 0.65f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "trackFill"
    )

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title   = { Text("Delete Request?", fontWeight = FontWeight.SemiBold) },
            text    = { Text("This will permanently delete your visit request. You'll need to register again.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    deleteVisitorRequest(navController, context)
                }) { Text("Delete", color = SemanticRed, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    LiquidGlassBackground(backgroundColor = BgDeep) {
        Box(modifier = Modifier.fillMaxSize()) {
            // The infinite, futuristic scanning laser animation
            ScanningLaserBackground()

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
                // Lottie
                LottieAnimation(
                    composition = composition,
                    progress    = { progress },
                    modifier    = Modifier.size(220.dp)
                )

                Spacer(Modifier.height(8.dp))

                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier            = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = "Request Submitted",
                            color      = TextPrimary,
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LiveDot(color = SemanticAmber)
                            Text(
                                text     = "Waiting for admin approval…",
                                color    = SemanticAmber,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text      = "You'll be redirected automatically",
                            color     = TextTertiary,
                            fontSize  = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))

                        GlossyDivider()
                        Spacer(Modifier.height(12.dp))

                        SectionLabel(text = "Review progress")
                        Spacer(Modifier.height(8.dp))

                        // Progress track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(trackProgress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(AccentIndigo, AccentBlue)
                                        )
                                    )
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text     = "Awaiting admin review",
                            color    = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                GlassButton(
                    text     = "Update Request",
                    style    = GlassButtonStyle.Primary,
                    onClick  = { navController.navigate("register?edit=true") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                GlassButton(
                    text    = "Delete Request",
                    style   = GlassButtonStyle.Danger,
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    }
}

private fun deleteVisitorRequest(
    navController: NavHostController,
    context: android.content.Context
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    FirebaseDatabase.getInstance()
        .getReference("visitorRequests")
        .orderByChild("uid").equalTo(uid).get()
        .addOnSuccessListener { snapshot ->
            snapshot.children.maxByOrNull {
                it.child("timestamp").getValue(Long::class.java) ?: 0L
            }?.ref?.removeValue()?.addOnSuccessListener {
                Toast.makeText(context, "Request deleted", Toast.LENGTH_SHORT).show()
                navController.navigate("intro") { popUpTo("pending") { inclusive = true } }
            }
        }
}

@Composable
fun ScanningLaserBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    // Animate a value from 0f to 1f and back
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Draw a faint high-tech grid
        val gridSize = 100f
        val gridColor = Color(0xFF00E5FF).copy(alpha = 0.15f) // Brightened significantly
        
        // Vertical lines
        var x = 0f
        while(x <= width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 2f
            )
            x += gridSize
        }
        
        // Horizontal lines
        var y = 0f
        while(y <= height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 2f
            )
            y += gridSize
        }

        // 2. Draw the scanning laser line
        val laserPos = height * laserY
        
        // Glow effect (thick gradient rect around the line)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF00E5FF).copy(alpha = 0.3f),
                    Color(0xFF00E5FF).copy(alpha = 0.8f),
                    Color(0xFF00E5FF).copy(alpha = 0.3f),
                    Color.Transparent
                ),
                startY = laserPos - 120f,
                endY = laserPos + 120f
            ),
            topLeft = Offset(0f, laserPos - 120f),
            size = Size(width, 240f)
        )
        
        // Solid core laser line
        drawLine(
            color = Color(0xFF00E5FF), // Pure, 100% opaque cyan
            start = Offset(0f, laserPos),
            end = Offset(width, laserPos),
            strokeWidth = 5f
        )
    }
}
