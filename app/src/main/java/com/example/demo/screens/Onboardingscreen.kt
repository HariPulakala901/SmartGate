package com.example.demo.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demo.R
import com.example.demo.ui.components.GlassButton
import com.example.demo.ui.components.GlassButtonStyle
import kotlinx.coroutines.launch

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String,
    val accentColor: Color,
    val overlayBottom: Color,
)

@Composable
fun OnboardingScreen(
    navController: NavHostController,
    onOnboardingComplete: () -> Unit  // ← added: called by Skip and Get Started
) {

    val pages = listOf(
        OnboardingPage(
            imageRes      = R.drawable.onboarding_bg_1,
            title         = "Smart Entry,\nZero Hassle",
            description   = "Register your visit in seconds using your Aadhaar or PAN card. No paperwork, no queues.",
            accentColor   = Color(0xFF4FC3F7),
            overlayBottom = Color(0xFF050D1A),
        ),
        OnboardingPage(
            imageRes      = R.drawable.onboarding_bg_2,
            title         = "Real-Time\nApproval Status",
            description   = "Know instantly when your visit is approved, pending, or flagged — right from your phone.",
            accentColor   = Color(0xFF81D4FA),
            overlayBottom = Color(0xFF04121E),
        ),
        OnboardingPage(
            imageRes      = R.drawable.onboarding_bg_3,
            title         = "Every Entry\nSecured",
            description   = "Advanced ID verification prevents proxy entries and keeps every visitor accountable.",
            accentColor   = Color(0xFFB39DDB),
            overlayBottom = Color(0xFF0A0618),
        ),
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope      = rememberCoroutineScope()

    // Orb pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val orbScale by infiniteTransition.animateFloat(
        initialValue  = 0.90f,
        targetValue   = 1.10f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // ── LAYER 1: Full-screen pager with background images ──────────────
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = pages[pageIndex]
            Box(modifier = Modifier.fillMaxSize()) {

                // Full-bleed illustration
                Image(
                    painter            = painterResource(id = page.imageRes),
                    contentDescription = null,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )

                // Bottom dark gradient fade
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.00f to Color.Transparent,
                                    0.38f to Color.Transparent,
                                    0.55f to page.overlayBottom.copy(alpha = 0.40f),
                                    0.70f to page.overlayBottom.copy(alpha = 0.72f),
                                    0.82f to page.overlayBottom.copy(alpha = 0.90f),
                                    1.00f to page.overlayBottom,
                                )
                            )
                        )
                )

                // Soft accent glow behind the card
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                page.accentColor.copy(alpha = 0.20f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.50f, size.height * 0.75f),
                            radius = size.width * 0.75f * orbScale
                        )
                    )
                }
            }
        }

        // ── LAYER 2: UI content on top ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            // Skip button — calls onOnboardingComplete instead of navigating directly
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onOnboardingComplete) {  // ← changed
                    Text(
                        text       = "Skip",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color      = Color.White.copy(alpha = 0.60f)
                    )
                }
            }

            // Push card to bottom ~35% of screen
            Spacer(modifier = Modifier.weight(1f))

            // ── Animated glass card ────────────────────────────────────────
            val currentPage = pages[pagerState.currentPage]

            var cardVisible by remember { mutableStateOf(false) }
            LaunchedEffect(pagerState.currentPage) {
                cardVisible = false
                kotlinx.coroutines.delay(60)
                cardVisible = true
            }
            val cardAlpha by animateFloatAsState(
                targetValue   = if (cardVisible) 1f else 0f,
                animationSpec = tween(420),
                label         = "cardAlpha"
            )
            val cardSlide by animateFloatAsState(
                targetValue   = if (cardVisible) 0f else 24f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label         = "cardSlide"
            )

            // Frosted glass card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        alpha        = cardAlpha
                        translationY = cardSlide
                    }
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    currentPage.accentColor.copy(alpha = 0.35f),
                                    currentPage.accentColor.copy(alpha = 0.10f),
                                    Color.Transparent,
                                )
                            ),
                            cornerRadius = CornerRadius(32.dp.toPx()),
                            size         = Size(size.width + 8.dp.toPx(), size.height + 8.dp.toPx()),
                            topLeft      = Offset(-4.dp.toPx(), -4.dp.toPx()),
                        )
                    }
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.White.copy(alpha = 0.09f),
                                Color.White.copy(alpha = 0.05f),
                            ),
                            start = Offset(0f, 0f),
                            end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .drawBehind {
                        val shine = Path()
                        shine.moveTo(0f, 0f)
                        shine.lineTo(size.width * 0.60f, 0f)
                        shine.lineTo(0f, size.height * 0.42f)
                        shine.close()
                        drawPath(
                            path  = shine,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.45f),
                                    currentPage.accentColor.copy(alpha = 0.35f),
                                    Color.White.copy(alpha = 0.10f),
                                    Color(0xFF7B68EE).copy(alpha = 0.18f),
                                )
                            ),
                            cornerRadius = CornerRadius(28.dp.toPx()),
                            style        = Stroke(width = 1.2.dp.toPx())
                        )
                    }
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text       = currentPage.title,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        textAlign  = TextAlign.Center,
                        lineHeight = 32.sp,
                        modifier   = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text       = currentPage.description,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color      = Color.White.copy(alpha = 0.68f),
                        textAlign  = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier   = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Dot indicators ─────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, page ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue   = if (isSelected) 24.dp else 7.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label         = "dotWidth_$index"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(7.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(
                                if (isSelected)
                                    Brush.horizontalGradient(
                                        listOf(
                                            page.accentColor,
                                            page.accentColor.copy(alpha = 0.55f)
                                        )
                                    )
                                else
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.28f),
                                            Color.White.copy(alpha = 0.28f)
                                        )
                                    )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Next / Get Started button ──────────────────────────────────
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                GlassButton(
                    text     = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                    style    = GlassButtonStyle.Primary,
                    onClick  = {
                        if (pagerState.currentPage == pages.size - 1) {
                            onOnboardingComplete()  // ← changed: callback handles prefs + navigation
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}