package com.example.demo.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demo.ui.components.GlassButton
import com.example.demo.ui.components.GlassButtonStyle
import com.example.demo.ui.components.GlassBanner
import com.example.demo.ui.components.GlossyDivider
import com.example.demo.ui.components.LiquidGlassBackground
import com.example.demo.ui.components.LiquidGlassCard
import com.example.demo.ui.components.LiveDot
import com.example.demo.ui.theme.SemanticAmber
import com.example.demo.ui.theme.BgDeep
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun Flaggedscreen(navController: NavHostController) {

    val uid     = FirebaseAuth.getInstance().currentUser?.uid
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

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
                    "APPROVED" -> navController.navigate("approved") { popUpTo("flagged") { inclusive = true } }
                    "REJECTED" -> navController.navigate("rejected") { popUpTo("flagged") { inclusive = true } }
                    "PENDING"  -> navController.navigate("pending")  { popUpTo("flagged") { inclusive = true } }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        query.addValueEventListener(listener)
        onDispose { query.removeEventListener(listener) }
    }

    LiquidGlassBackground(backgroundColor = BgDeep) {
        Box(modifier = Modifier.fillMaxSize()) {
            AmberCautionBackground()

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
                Text(text = "🚩", fontSize = 60.sp)

                Spacer(Modifier.height(16.dp))

                Text(
                    text       = "Request Flagged",
                    color      = SemanticAmber,
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text      = "Your request is under admin review",
                    color     = TextSecondary,
                    fontSize  = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        GlassBanner(
                            text  = "⚠️ This happens when the ID you provided was previously used with a different name. An admin will verify your identity before making a decision.",
                            color = SemanticAmber
                        )

                        Spacer(Modifier.height(16.dp))
                        GlossyDivider()
                        Spacer(Modifier.height(12.dp))

                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LiveDot(color = SemanticAmber, size = 7.dp)
                            Text(
                                text     = "Waiting for admin decision",
                                color    = SemanticAmber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text      = "You will be redirected automatically once the admin reviews your request.",
                            color     = TextTertiary,
                            fontSize  = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                GlassButton(
                    text     = "Back to Home",
                    style    = GlassButtonStyle.Secondary,
                    onClick  = {
                        navController.navigate("intro") {
                            popUpTo("flagged") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    }
}

@Composable
fun AmberCautionBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "amberCaution")
    
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val amber = Color(0xFFFFB300)
        
        val center = Offset(width / 2f, height * 0.35f)
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    amber.copy(alpha = auraAlpha),
                    amber.copy(alpha = auraAlpha * 0.2f),
                    Color.Transparent
                ),
                center = center,
                radius = width * 0.7f
            ),
            center = center,
            radius = width * 0.7f
        )
        
        val rippleRadius = width * rippleScale
        val rippleAlpha = 1f - rippleScale
        
        drawCircle(
            color = amber.copy(alpha = rippleAlpha * 0.5f),
            center = center,
            radius = rippleRadius,
            style = Stroke(width = 5f)
        )
        
        val secondRippleScale = (rippleScale + 0.5f) % 1f
        val secondRippleAlpha = 1f - secondRippleScale
        drawCircle(
            color = amber.copy(alpha = secondRippleAlpha * 0.5f),
            center = center,
            radius = width * secondRippleScale,
            style = Stroke(width = 5f)
        )
    }
}
