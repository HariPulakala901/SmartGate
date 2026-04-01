//package com.example.demo.screens
//
//import android.content.Context
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.drawWithContent
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.example.demo.R
//import com.example.demo.ui.components.GlassButton
//import com.example.demo.ui.components.GlassButtonStyle
//import com.example.demo.ui.components.GlossyDivider
//import com.example.demo.ui.components.LiquidGlassBackground
//import com.example.demo.ui.components.LiquidGlassCard
//import com.example.demo.ui.components.LiveDot
//import com.example.demo.ui.theme.AccentBlue
//import com.example.demo.ui.theme.BgDeep
//import com.example.demo.ui.theme.SemanticAmber
//import com.example.demo.ui.theme.SemanticGreen
//import com.example.demo.ui.theme.TextPrimary
//import com.example.demo.ui.theme.TextSecondary
//import com.example.demo.ui.theme.TextTertiary
//import com.google.firebase.auth.FirebaseAuth
//
//@Composable
//fun Mainscreen(navController: NavHostController) {
//
//    val context = LocalContext.current
//    var isCheckingStatus by remember { mutableStateOf(false) }
//    val auth = FirebaseAuth.getInstance()
//
//    // Subtle floating animation for the glass icon card
//    val infiniteTransition = rememberInfiniteTransition(label = "float")
//    val iconFloat by infiniteTransition.animateFloat(
//        initialValue  = 0f,
//        targetValue   = -6f,
//        animationSpec = infiniteRepeatable(
//            animation  = tween(2200, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "iconFloat"
//    )
//
//    // The deep background colour — same as BgDeep so the gradient fade
//    // blends seamlessly into the rest of the app
//    val bgColor = BgDeep
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // ── Layer 1: Hero illustration — fills roughly top 52% of screen ──
//        // Same composition as Marga Disha: image at top, dark gradient
//        // fades it out toward the bottom so content reads clearly.
//        Image(
//            painter            = painterResource(id = R.drawable.bg_smartgate),
//            contentDescription = null,
//            contentScale       = ContentScale.Crop,
//            modifier           = Modifier
//                .fillMaxWidth()
//                .fillMaxSize(0.52f)          // top 52% of the screen height
//                .align(Alignment.TopCenter)
//                .drawWithContent {
//                    drawContent()
//                    // Four-stop gradient: fully transparent at top,
//                    // fades through the illustration, fully opaque at bottom
//                    // — identical technique to what Marga Disha uses
//                    drawRect(
//                        brush = Brush.verticalGradient(
//                            colorStops = arrayOf(
//                                0.00f to Color.Transparent,
//                                0.30f to Color.Transparent,
//                                0.72f to bgColor.copy(alpha = 0.55f),
//                                0.88f to bgColor.copy(alpha = 0.82f),
//                                1.00f to bgColor
//                            )
//                        )
//                    )
//                }
//        )
//
//        // ── Layer 2: Ambient blobs + scrollable content ────────────────────
//        // LiquidGlassBackground draws the blobs but with a transparent base
//        // so the hero image shows through at the top.
//        LiquidGlassBackground(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
//                    .padding(horizontal = 24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                // Push content down so the hero image is visible above it —
//                // same proportion as Marga Disha (image takes ~46% height,
//                // content starts just below the midpoint)
//                Spacer(Modifier.height(240.dp))
//
//                // ── App icon — floats over the image boundary ─────────────
//                LiquidGlassCard(
//                    modifier     = Modifier
//                        .size(88.dp)
//                        .graphicsLayer { translationY = iconFloat },
//                    cornerRadius = 28.dp
//                ) {
//                    Column(
//                        modifier            = Modifier.fillMaxSize(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Text(text = "🔐", fontSize = 36.sp)
//                    }
//                }
//
//                Spacer(Modifier.height(16.dp))
//
//                // ── Title ──────────────────────────────────────────────────
//                Text(
//                    text          = "SmartGate",
//                    color         = TextPrimary,
//                    fontSize      = 34.sp,
//                    fontWeight    = FontWeight.Bold,
//                    letterSpacing = (-0.5).sp
//                )
//                Spacer(Modifier.height(4.dp))
//                Text(
//                    text      = "Visitor Management System",
//                    color     = TextSecondary,
//                    fontSize  = 15.sp,
//                    textAlign = TextAlign.Center
//                )
//                Spacer(Modifier.height(3.dp))
//                Text(
//                    text      = "Register your visit · Wait for approval",
//                    color     = TextTertiary,
//                    fontSize  = 13.sp,
//                    textAlign = TextAlign.Center
//                )
//
//                Spacer(Modifier.height(28.dp))
//
//                // ── Stats row ──────────────────────────────────────────────
//                Row(
//                    modifier              = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    LiquidGlassCard(modifier = Modifier.weight(1f)) {
//                        Column(
//                            modifier            = Modifier.padding(14.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Row(
//                                verticalAlignment     = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(6.dp)
//                            ) {
//                                LiveDot(color = SemanticGreen)
//                                Text(
//                                    text       = "Active",
//                                    color      = SemanticGreen,
//                                    fontSize   = 11.sp,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                            }
//                            Spacer(Modifier.height(4.dp))
//                            Text(
//                                text       = "6",
//                                color      = TextPrimary,
//                                fontSize   = 26.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Text(text = "Zones", color = TextTertiary, fontSize = 11.sp)
//                        }
//                    }
//
//                    LiquidGlassCard(modifier = Modifier.weight(1f)) {
//                        Column(
//                            modifier            = Modifier.padding(14.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Row(
//                                verticalAlignment     = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(6.dp)
//                            ) {
//                                LiveDot(color = AccentBlue)
//                                Text(
//                                    text       = "Today",
//                                    color      = AccentBlue,
//                                    fontSize   = 11.sp,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                            }
//                            Spacer(Modifier.height(4.dp))
//                            Text(
//                                text       = "24",
//                                color      = TextPrimary,
//                                fontSize   = 26.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Text(text = "Inside", color = TextTertiary, fontSize = 11.sp)
//                        }
//                    }
//
//                    LiquidGlassCard(modifier = Modifier.weight(1f)) {
//                        Column(
//                            modifier            = Modifier.padding(14.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Row(
//                                verticalAlignment     = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(6.dp)
//                            ) {
//                                LiveDot(color = SemanticAmber)
//                                Text(
//                                    text       = "Pending",
//                                    color      = SemanticAmber,
//                                    fontSize   = 11.sp,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                            }
//                            Spacer(Modifier.height(4.dp))
//                            Text(
//                                text       = "7",
//                                color      = TextPrimary,
//                                fontSize   = 26.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Text(text = "Queue", color = TextTertiary, fontSize = 11.sp)
//                        }
//                    }
//                }
//
//                Spacer(Modifier.height(24.dp))
//                GlossyDivider()
//                Spacer(Modifier.height(20.dp))
//
//                // ── CTA buttons ────────────────────────────────────────────
//                GlassButton(
//                    text    = if (isCheckingStatus) "Checking…" else "Visitor Registration",
//                    style   = GlassButtonStyle.Primary,
//                    enabled = !isCheckingStatus,
//                    onClick = {
//                        isCheckingStatus = true
//                        val currentUser = auth.currentUser
//
//                        fun doCheck(uid: String) {
//                            context.getSharedPreferences("visitor_prefs", Context.MODE_PRIVATE)
//                                .edit().putString("uid", uid).apply()
//                            checkRequestStatus(
//                                uid         = uid,
//                                onApproved  = { isCheckingStatus = false; navController.navigate("approved") },
//                                onPending   = { isCheckingStatus = false; navController.navigate("pending") },
//                                onRejected  = { isCheckingStatus = false; navController.navigate("rejected") },
//                                onFlagged   = { isCheckingStatus = false; navController.navigate("flagged") },
//                                onNoRequest = { isCheckingStatus = false; navController.navigate("register") }
//                            )
//                        }
//
//                        if (currentUser != null) {
//                            doCheck(currentUser.uid)
//                        } else {
//                            auth.signInAnonymously()
//                                .addOnSuccessListener { doCheck(it.user!!.uid) }
//                                .addOnFailureListener { isCheckingStatus = false }
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                if (isCheckingStatus) {
//                    Spacer(Modifier.height(10.dp))
//                    CircularProgressIndicator(
//                        color       = AccentBlue,
//                        modifier    = Modifier.size(24.dp),
//                        strokeWidth = 2.dp
//                    )
//                }
//
//                Spacer(Modifier.height(12.dp))
//
//                GlassButton(
//                    text     = "Visit History",
//                    style    = GlassButtonStyle.Secondary,
//                    onClick  = { navController.navigate("history") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                if (auth.currentUser != null) {
//                    Spacer(Modifier.height(12.dp))
//                    GlassButton(
//                        text    = "Sign Out",
//                        style   = GlassButtonStyle.Danger,
//                        onClick = {
//                            auth.signOut()
//                            context.getSharedPreferences("visitor_prefs", Context.MODE_PRIVATE)
//                                .edit().clear().apply()
//                            navController.navigate("intro") {
//                                popUpTo("intro") { inclusive = true }
//                            }
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//
//                Spacer(Modifier.height(32.dp))
//
//                Text(
//                    text      = "Every visit verified · Every entry secured",
//                    color     = TextTertiary,
//                    fontSize  = 13.sp,
//                    textAlign = TextAlign.Center
//                )
//
//                Spacer(Modifier.height(32.dp))
//            }
//        }
//    }
//}




package com.example.demo.screens

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demo.R
import com.example.demo.ui.components.GlassButton
import com.example.demo.ui.components.GlassButtonStyle
import com.example.demo.ui.components.GlossyDivider
import com.example.demo.ui.components.LiquidGlassBackground
import com.example.demo.ui.components.LiquidGlassCard
import com.example.demo.ui.theme.AccentBlue
import com.example.demo.ui.theme.BgDeep
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Mainscreen(navController: NavHostController) {

    val context = LocalContext.current
    var isCheckingStatus by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val iconFloat by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -6f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconFloat"
    )

    val bgColor = BgDeep

    // ── Root Box: image sits here at the very bottom of the z-stack ────────
    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {

        // ── Layer 1: Hero image — drawn FIRST so everything else is on top ──
        Image(
            painter            = painterResource(id = R.drawable.bg_smartgate),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.55f)
                .align(Alignment.TopCenter)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Transparent,
                                0.35f to Color.Transparent,
                                0.70f to bgColor.copy(alpha = 0.60f),
                                0.88f to bgColor.copy(alpha = 0.88f),
                                1.00f to bgColor
                            )
                        )
                    )
                }
        )

        // ── Layer 2: LiquidGlassBackground with transparent container ───────
        // IMPORTANT: LiquidGlassBackground must NOT draw its own solid bg.
        // Pass a transparent-friendly modifier so the image bleeds through.
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LiquidGlassBackground(
                modifier = Modifier.fillMaxSize(),
                backgroundColor = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Space so image is visible above the content
                    Spacer(Modifier.height(340.dp))

                    // ── App icon ─────────────────────────────────────────────
                    LiquidGlassCard(
                        modifier     = Modifier
                            .size(88.dp)
                            .graphicsLayer { translationY = iconFloat },
                        cornerRadius = 28.dp
                    ) {
                        Column(
                            modifier            = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🔐", fontSize = 36.sp)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Title ────────────────────────────────────────────────
                    Text(
                        text          = "SmartGate",
                        color         = TextPrimary,
                        fontSize      = 34.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text      = "Visitor Management System",
                        color     = TextSecondary,
                        fontSize  = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text      = "Register your visit · Wait for approval",
                        color     = TextTertiary,
                        fontSize  = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(32.dp))
                    GlossyDivider()
                    Spacer(Modifier.height(24.dp))

                    // ── CTA buttons ──────────────────────────────────────────
                    GlassButton(
                        text    = if (isCheckingStatus) "Checking…" else "Visitor Registration",
                        style   = GlassButtonStyle.Primary,
                        enabled = !isCheckingStatus,
                        onClick = {
                            isCheckingStatus = true
                            val currentUser = auth.currentUser

                            fun doCheck(uid: String) {
                                context.getSharedPreferences("visitor_prefs", Context.MODE_PRIVATE)
                                    .edit().putString("uid", uid).apply()
                                checkRequestStatus(
                                    uid         = uid,
                                    onApproved  = { isCheckingStatus = false; navController.navigate("approved") },
                                    onPending   = { isCheckingStatus = false; navController.navigate("pending") },
                                    onRejected  = { isCheckingStatus = false; navController.navigate("rejected") },
                                    onFlagged   = { isCheckingStatus = false; navController.navigate("flagged") },
                                    onNoRequest = { isCheckingStatus = false; navController.navigate("register") }
                                )
                            }

                            if (currentUser != null) {
                                doCheck(currentUser.uid)
                            } else {
                                auth.signInAnonymously()
                                    .addOnSuccessListener { doCheck(it.user!!.uid) }
                                    .addOnFailureListener { isCheckingStatus = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isCheckingStatus) {
                        Spacer(Modifier.height(10.dp))
                        CircularProgressIndicator(
                            color       = AccentBlue,
                            modifier    = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    GlassButton(
                        text     = "Visit History",
                        style    = GlassButtonStyle.Secondary,
                        onClick  = { navController.navigate("history") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (auth.currentUser != null) {
                        Spacer(Modifier.height(12.dp))
                        GlassButton(
                            text    = "Sign Out",
                            style   = GlassButtonStyle.Danger,
                            onClick = {
                                auth.signOut()
                                context.getSharedPreferences("visitor_prefs", Context.MODE_PRIVATE)
                                    .edit().clear().apply()
                                navController.navigate("intro") {
                                    popUpTo("intro") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text      = "Every visit verified · Every entry secured",
                        color     = TextTertiary,
                        fontSize  = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}