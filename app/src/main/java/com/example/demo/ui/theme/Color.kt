package com.example.demo.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Liquid Glass — Background layers ───────────────────────────────────────
val BgDeep        = Color(0xFF060B16)   // deepest base
val BgDark        = Color(0xFF0A1020)   // card base
val BgMid         = Color(0xFF0F1828)   // elevated surface
val BgRaised      = Color(0xFF141F30)   // top-most surface

// ─── Glass tints (used for card overlays) ───────────────────────────────────
val GlassWhite10  = Color(0x1AFFFFFF)   // 10% white — base glass fill
val GlassWhite15  = Color(0x26FFFFFF)   // 15% white — elevated glass
val GlassWhite20  = Color(0x33FFFFFF)   // 20% white — active / pressed
val GlassWhite06  = Color(0x0FFFFFFF)   // 6%  white — subtle tint

// ─── Glass borders (specular edge highlights) ────────────────────────────────
val GlassBorder18 = Color(0x2EFFFFFF)   // 18% — default card border
val GlassBorder35 = Color(0x59FFFFFF)   // 35% — bright specular top edge
val GlassBorder50 = Color(0x80FFFFFF)   // 50% — active / focused border

// ─── Ambient blobs (behind glass — what it refracts) ────────────────────────
val AmbientPurple = Color(0x665E5CE6)   // iOS indigo blob
val AmbientCyan   = Color(0x6632ADE8)   // iOS blue blob
val AmbientViolet = Color(0x66BF5AF2)   // iOS purple blob
val AmbientGreen  = Color(0x4430D158)   // iOS green blob

// ─── Accent colours (iOS 26 system palette) ─────────────────────────────────
val AccentBlue    = Color(0xFF34AADC)   // iOS system blue
val AccentIndigo  = Color(0xFF5E5CE6)   // iOS system indigo
val AccentPurple  = Color(0xFFBF5AF2)   // iOS system purple

// ─── Semantic colours ────────────────────────────────────────────────────────
val SemanticGreen  = Color(0xFF30D158)  // iOS system green  (approved / active)
val SemanticAmber  = Color(0xFFFF9F0A)  // iOS system amber  (pending / flagged)
val SemanticRed    = Color(0xFFFF453A)  // iOS system red    (rejected / error)
val SemanticBlue   = Color(0xFF32ADE8)  // iOS system cyan   (inside / info)

// ─── Tinted glass tones for status cards ────────────────────────────────────
val GreenGlass12   = Color(0x1F30D158)
val GreenBorder35  = Color(0x5930D158)
val AmberGlass12   = Color(0x1FFF9F0A)
val AmberBorder35  = Color(0x59FF9F0A)
val RedGlass12     = Color(0x1FFF453A)
val RedBorder35    = Color(0x59FF453A)
val BlueGlass12    = Color(0x1F32ADE8)
val BlueBorder35   = Color(0x5932ADE8)
val IndigoGlass20  = Color(0x335E5CE6)
val IndigoBorder40 = Color(0x665E5CE6)

// ─── Text ────────────────────────────────────────────────────────────────────
val TextPrimary   = Color(0xFFFFFFFF)
val TextSecondary = Color(0x99FFFFFF)   // 60%
val TextTertiary  = Color(0x61FFFFFF)   // 38%
val TextDisabled  = Color(0x3DFFFFFF)   // 24%