package com.goskar.boardgame.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// =============================================================================
// TYPOGRAPHY — BoardGame
// -----------------------------------------------------------------------------
// Jeden obiekt dla obu motywów, systemowy font.
// Theme.kt mapuje style na MaterialTheme.typography per motyw.
// =============================================================================

object BoardGameTypography {

    // --- Display / Headline (oba motywy) ---

    val DisplayLg = TextStyle(
        fontWeight    = FontWeight.Bold,
        fontSize      = 32.sp,
        lineHeight    = 40.sp,
        letterSpacing = (-0.64).sp        // -0.02em × 32
    )
    val DisplayLgMobile = TextStyle(      // light
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = (-0.56).sp
    )
    val DisplayLgMobileDark = TextStyle(  // dark
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = (-0.56).sp
    )
    val HeadlineMd = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        lineHeight = 32.sp
    )
    val TitleLg = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 28.sp
    )

    // --- Body (light) ---

    val BodyLg = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    )
    val BodySm = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    )

    // --- Body (dark) ---

    val BodyLgDark = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    )
    val BodyMdDark = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    )

    // --- Labels (light) ---

    val LabelCaps = TextStyle(
        fontWeight    = FontWeight.Bold,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.60.sp           // 0.05em × 12
    )

    // --- Labels (dark) ---

    val LabelLg = TextStyle(
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.60.sp
    )
    val LabelMd = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    )
}

object BoardGameDarkTypography {
    val DisplayLg   = BoardGameTypography.DisplayLg
    val DisplayMd   = BoardGameTypography.DisplayLgMobileDark
    val HeadlineMd  = BoardGameTypography.HeadlineMd
    val TitleLg     = BoardGameTypography.TitleLg
    val BodyLg      = BoardGameTypography.BodyLgDark
    val BodyMd      = BoardGameTypography.BodyMdDark
    val LabelLg     = BoardGameTypography.LabelLg
    val LabelMd     = BoardGameTypography.LabelMd
    val HeadlineLgMobile = BoardGameTypography.DisplayLgMobileDark
}
