package com.example.demo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo.ui.theme.AccentBlue
import com.example.demo.ui.theme.AccentIndigo
import com.example.demo.ui.theme.AmberBorder35
import com.example.demo.ui.theme.AmberGlass12
import com.example.demo.ui.theme.BlueBorder35
import com.example.demo.ui.theme.BlueGlass12
import com.example.demo.ui.theme.BgDark
import com.example.demo.ui.theme.GlassBorder18
import com.example.demo.ui.theme.GlassBorder35
import com.example.demo.ui.theme.GlassWhite10
import com.example.demo.ui.theme.GlassWhite15
import com.example.demo.ui.theme.GlassWhite20
import com.example.demo.ui.theme.GreenBorder35
import com.example.demo.ui.theme.GreenGlass12
import com.example.demo.ui.theme.RedBorder35
import com.example.demo.ui.theme.RedGlass12
import com.example.demo.ui.theme.SemanticAmber
import com.example.demo.ui.theme.SemanticBlue
import com.example.demo.ui.theme.SemanticGreen
import com.example.demo.ui.theme.SemanticRed
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary

// ─────────────────────────────────────────────────────────────────────────────
// LiquidGlassCard
// A frosted-glass surface with a specular top-edge highlight.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    fillColor: Color = GlassWhite10,
    borderColor: Color = GlassBorder18,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(fillColor)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(GlassBorder35, borderColor, Color.Transparent)
                ),
                shape = shape
            )
            .drawBehind {
                // Specular top-edge shimmer line
                drawLine(
                    brush  = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    start       = Offset(size.width * 0.1f, 1f),
                    end         = Offset(size.width * 0.9f, 1f),
                    strokeWidth = 1.5f
                )
            }
    ) {
        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassButton  — replaces your old GradientButton everywhere
// ─────────────────────────────────────────────────────────────────────────────
enum class GlassButtonStyle { Primary, Secondary, Danger }

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: GlassButtonStyle = GlassButtonStyle.Primary,
    leadingIcon: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue    = if (pressed) 0.97f else 1f,
        animationSpec  = tween(100),
        label          = "btnScale"
    )

    val (fillBrush, borderColor) = when {
        !enabled -> Pair(
            Brush.horizontalGradient(listOf(GlassWhite10, GlassWhite10)),
            GlassBorder18
        )
        style == GlassButtonStyle.Primary -> Pair(
            Brush.linearGradient(
                colors = listOf(
                    AccentBlue.copy(alpha = 0.35f),
                    AccentIndigo.copy(alpha = 0.35f)
                )
            ),
            AccentBlue.copy(alpha = 0.55f)
        )
        style == GlassButtonStyle.Danger -> Pair(
            Brush.horizontalGradient(listOf(RedGlass12, RedGlass12)),
            SemanticRed.copy(alpha = 0.4f)
        )
        else -> Pair(
            Brush.horizontalGradient(listOf(GlassWhite15, GlassWhite10)),
            GlassBorder18
        )
    }

    val textColor = when {
        !enabled                           -> TextTertiary
        style == GlassButtonStyle.Danger   -> SemanticRed
        else                               -> TextPrimary
    }

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .height(54.dp)
            .clip(shape)
            .background(fillBrush)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(GlassBorder35, borderColor, borderColor)
                ),
                shape = shape
            )
            .drawBehind {
                drawLine(
                    brush  = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    start       = Offset(size.width * 0.2f, 1f),
                    end         = Offset(size.width * 0.8f, 1f),
                    strokeWidth = 1.5f
                )
            }
            .clickable(
                enabled           = enabled,
                interactionSource = interactionSource,
                indication        = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Text(text = leadingIcon, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text       = text,
                color      = textColor,
                fontSize   = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// StatusBadge  — pill with glass tint matching the status colour
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bg, border, text, label) = when (status) {
        "APPROVED" -> listOf(GreenGlass12, GreenBorder35, SemanticGreen,  "✅ Approved")
        "INSIDE"   -> listOf(BlueGlass12,  BlueBorder35,  SemanticBlue,   "📡 Inside")
        "EXITED"   -> listOf(GreenGlass12, GreenBorder35, SemanticGreen,  "🚪 Exited")
        "REJECTED" -> listOf(RedGlass12,   RedBorder35,   SemanticRed,    "❌ Rejected")
        "PENDING"  -> listOf(AmberGlass12, AmberBorder35, SemanticAmber,  "⏳ Pending")
        "FLAGGED"  -> listOf(AmberGlass12, AmberBorder35, SemanticAmber,  "🚩 Flagged")
        else       -> listOf(GlassWhite10, GlassBorder18, TextSecondary,  status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bg as Color)
            .border(1.dp, border as Color, RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text       = label as String,
            color      = text as Color,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LiveDot  — animated pulsing presence indicator
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LiveDot(color: Color = SemanticGreen, size: Dp = 8.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "liveDot")
    val alpha by infiniteTransition.animateFloat(
        initialValue  = 0.4f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )
    Box(
        modifier = Modifier
            .size(size)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// SectionLabel  — e.g. "TRACKING SETUP" (all-caps small label)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text.uppercase(),
        color      = TextTertiary,
        fontSize   = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        modifier   = modifier
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// GlossyDivider  — thin gradient line between sections
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GlossyDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        AccentBlue.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// TrackingStatusRow  — icon + label + tick/cross (used on approved screen)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TrackingStatusRow(
    icon: @Composable () -> Unit,
    label: String,
    isOk: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        icon()
        Text(
            text     = label,
            color    = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isOk) GreenGlass12 else RedGlass12),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = if (isOk) "✓" else "✗",
                color    = if (isOk) SemanticGreen else SemanticRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// RadarWidget  — pulsing Wi-Fi zone radar shown on approvedscreen / tracking
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RadarWidget(zoneName: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    // Three rings at staggered offsets
    val ring1 by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label         = "ring1"
    )
    val ring2 by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2400, 800, easing = LinearEasing)),
        label         = "ring2"
    )
    val ring3 by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2400, 1600, easing = LinearEasing)),
        label         = "ring3"
    )

    val centerGlow by infiniteTransition.animateFloat(
        initialValue  = 0.6f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centerGlow"
    )

    Column(
        modifier            = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            // Expanding rings drawn in drawBehind
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        listOf(ring1, ring2, ring3).forEach { progress ->
                            val radius  = (size.minDimension / 2f) * (0.3f + progress * 0.7f)
                            val opacity = (1f - progress).coerceIn(0f, 1f) * 0.6f
                            drawCircle(
                                color  = AccentBlue.copy(alpha = opacity),
                                radius = radius,
                                center = center,
                                style  = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 1.5f
                                )
                            )
                        }
                    }
            )
            // Center dot
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AccentBlue.copy(alpha = centerGlow),
                                AccentBlue.copy(alpha = 0.2f)
                            )
                        ),
                        CircleShape
                    )
            )
        }

        Spacer(Modifier.height(10.dp))

        if (zoneName.isNotEmpty()) {
            Text(
                text       = zoneName,
                color      = TextPrimary,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center
            )
        } else {
            Text(
                text      = "Detecting zone…",
                color     = TextSecondary,
                fontSize  = 15.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// WifiSignalBars  — 5-bar signal strength indicator
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun WifiSignalBars(rssi: Int, modifier: Modifier = Modifier) {
    // Map RSSI to 1-5 bars: -50 or better = 5 bars, -80 or worse = 1 bar
    val bars = when {
        rssi >= -50 -> 5
        rssi >= -60 -> 4
        rssi >= -67 -> 3
        rssi >= -75 -> 2
        else        -> 1
    }
    val heights = listOf(6.dp, 9.dp, 13.dp, 17.dp, 22.dp)

    Row(
        modifier          = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        heights.forEachIndexed { index, barHeight ->
            val active = index < bars
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (active) SemanticGreen else GlassWhite10
                    )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassTextField  — styled OutlinedTextField wrapper
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun glassTextFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = AccentBlue.copy(alpha = 0.8f),
    unfocusedBorderColor    = GlassBorder18,
    focusedLabelColor       = AccentBlue,
    unfocusedLabelColor     = TextTertiary,
    cursorColor             = AccentBlue,
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    focusedContainerColor   = GlassWhite10,
    unfocusedContainerColor = GlassWhite10.copy(alpha = 0.5f),
    focusedLeadingIconColor   = AccentBlue,
    unfocusedLeadingIconColor = TextTertiary,
    focusedTrailingIconColor  = AccentBlue,
    unfocusedTrailingIconColor = TextTertiary,
    focusedSupportingTextColor   = TextTertiary,
    unfocusedSupportingTextColor = TextTertiary,
    errorSupportingTextColor     = SemanticRed,
    errorBorderColor             = SemanticRed.copy(alpha = 0.7f),
    errorLabelColor              = SemanticRed,
    errorTextColor               = TextPrimary
)

// ─────────────────────────────────────────────────────────────────────────────
// GlassChip  — ID type selector chip (Aadhaar / PAN)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GlassChip(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor     = if (selected && enabled) BlueGlass12 else GlassWhite10.copy(alpha = 0.4f)
    val borderColor = if (selected && enabled) AccentBlue.copy(alpha = 0.6f)
    else GlassBorder18
    val textColor   = if (selected && enabled) AccentBlue
    else if (!enabled) TextTertiary
    else TextSecondary

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(13.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = textColor,
            fontSize   = 13.sp,
            fontWeight = if (selected && enabled) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassBanner  — coloured info / warning / error banner inside a card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GlassBanner(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text       = text,
            color      = color,
            fontSize   = 12.sp,
            lineHeight = 18.sp
        )
    }
}