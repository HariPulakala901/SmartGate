package com.example.demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.demo.ui.theme.AmbientCyan
import com.example.demo.ui.theme.AmbientGreen
import com.example.demo.ui.theme.AmbientPurple
import com.example.demo.ui.theme.AmbientViolet
import com.example.demo.ui.theme.BgDeep

// ─────────────────────────────────────────────────────────────────────────────
// LiquidGlassBackground
//
// Wraps every screen.  Draws:
//   1. Deep base colour
//   2. Four soft ambient blobs (purple, cyan, violet, green) — these are what
//      the glass cards "refract", creating the iOS 26 depth effect.
//
// Usage:
//   LiquidGlassBackground {
//       /* your screen content */
//   }
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BgDeep,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .drawBehind {
                val w = size.width
                val h = size.height

                // Blob 1 — top-left purple (iOS indigo)
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors = listOf(AmbientPurple, Color.Transparent),
                        center = Offset(x = w * 0.1f, y = h * 0.05f),
                        radius = w * 0.7f
                    ),
                    radius = w * 0.7f,
                    center = Offset(x = w * 0.1f, y = h * 0.05f)
                )

                // Blob 2 — top-right cyan (iOS blue)
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors = listOf(AmbientCyan, Color.Transparent),
                        center = Offset(x = w * 0.9f, y = h * 0.25f),
                        radius = w * 0.65f
                    ),
                    radius = w * 0.65f,
                    center = Offset(x = w * 0.9f, y = h * 0.25f)
                )

                // Blob 3 — lower-left violet (iOS purple)
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors = listOf(AmbientViolet, Color.Transparent),
                        center = Offset(x = w * 0.15f, y = h * 0.75f),
                        radius = w * 0.6f
                    ),
                    radius = w * 0.6f,
                    center = Offset(x = w * 0.15f, y = h * 0.75f)
                )

                // Blob 4 — bottom-right green (iOS green)
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors = listOf(AmbientGreen, Color.Transparent),
                        center = Offset(x = w * 0.85f, y = h * 0.88f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(x = w * 0.85f, y = h * 0.88f)
                )
            }
    ) {
        content()
    }
}