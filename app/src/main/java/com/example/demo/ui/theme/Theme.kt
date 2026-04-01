package com.example.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// SmartGate always runs in dark mode — Liquid Glass is a dark-first design
private val LiquidGlassDarkScheme = darkColorScheme(
    primary          = AccentBlue,
    onPrimary        = Color.White,
    primaryContainer = IndigoGlass20,
    onPrimaryContainer = TextPrimary,

    secondary        = AccentIndigo,
    onSecondary      = Color.White,
    secondaryContainer = GlassWhite10,
    onSecondaryContainer = TextPrimary,

    tertiary         = AccentPurple,
    onTertiary       = Color.White,

    background       = BgDeep,
    onBackground     = TextPrimary,

    surface          = BgDark,
    onSurface        = TextPrimary,
    surfaceVariant   = BgMid,
    onSurfaceVariant = TextSecondary,

    outline          = GlassBorder18,
    outlineVariant   = GlassBorder35,

    error            = SemanticRed,
    onError          = Color.White,
    errorContainer   = RedGlass12,
    onErrorContainer = SemanticRed,

    scrim            = Color(0xCC000000)
)

@Composable
fun SmartGateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LiquidGlassDarkScheme,
        typography  = AppTypography,
        content     = content
    )
}