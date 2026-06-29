package com.goskar.boardgame.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// =============================================================================
// FONT FAMILIES
// -----------------------------------------------------------------------------
// TODO: Dodaj fonty do res/font/ i odkomentuj poniższe definicje.
//
// Montserrat: https://fonts.google.com/specimen/Montserrat
//   res/font/montserrat_regular.ttf
//   res/font/montserrat_medium.ttf
//   res/font/montserrat_semibold.ttf
//   res/font/montserrat_bold.ttf
//
// Inter: https://fonts.google.com/specimen/Inter
//   res/font/inter_regular.ttf
//   res/font/inter_medium.ttf
//   res/font/inter_semibold.ttf
//   res/font/inter_bold.ttf
// =============================================================================

val MontserratFamily: FontFamily = FontFamily.Default
//    FontFamily(
//        Font(R.font.montserrat_regular,  FontWeight.Normal),
//        Font(R.font.montserrat_medium,   FontWeight.Medium),
//        Font(R.font.montserrat_semibold, FontWeight.SemiBold),
//        Font(R.font.montserrat_bold,     FontWeight.Bold),
//    )

val InterFamily: FontFamily = FontFamily.Default
//    FontFamily(
//        Font(R.font.inter_regular,  FontWeight.Normal),
//        Font(R.font.inter_medium,   FontWeight.Medium),
//        Font(R.font.inter_semibold, FontWeight.SemiBold),
//        Font(R.font.inter_bold,     FontWeight.Bold),
//    )

// =============================================================================
// TYPOGRAPHY — BoardGame
// -----------------------------------------------------------------------------
// Jeden obiekt dla obu motywów.
// Light używa Inter dla body/label, dark używa Montserrat wszędzie.
// Theme.kt mapuje style na MaterialTheme.typography per motyw.
// =============================================================================

object BoardGameTypography {

    // --- Display / Headline — Montserrat (oba motywy) ---

    val DisplayLg = TextStyle(
        fontFamily    = MontserratFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 32.sp,
        lineHeight    = 40.sp,
        letterSpacing = (-0.64).sp        // -0.02em × 32
    )
    val DisplayLgMobile = TextStyle(      // light
        fontFamily    = MontserratFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = (-0.56).sp
    )
    val DisplayLgMobileDark = TextStyle(  // dark (26sp per spec)
        fontFamily    = MontserratFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 26.sp,
        lineHeight    = 32.sp,
        letterSpacing = (-0.52).sp
    )
    val HeadlineMd = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        lineHeight = 32.sp
    )
    val TitleLg = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 28.sp
    )

    // --- Body — Inter (light) ---

    val BodyLg = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    )
    val BodySm = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    )

    // --- Body — Montserrat (dark) ---

    val BodyLgDark = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    )
    val BodyMdDark = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    )

    // --- Labels — Inter (light) ---

    val LabelCaps = TextStyle(
        fontFamily    = InterFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.60.sp           // 0.05em × 12
    )

    // --- Labels — Montserrat (dark) ---

    val LabelLg = TextStyle(
        fontFamily    = MontserratFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.60.sp
    )
    val LabelMd = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 14.sp
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
