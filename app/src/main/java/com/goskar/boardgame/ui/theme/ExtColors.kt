package com.goskar.boardgame.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Extended semantic colors that Material3's [androidx.compose.material3.ColorScheme] doesn't cover
 * (currently just the "success" green). Provided by [BoardGameTheme] via [LocalBoardGameExtColors]
 * and read through [appExt] — so components pick the right value automatically from the active
 * theme, with no `isSystemInDarkTheme()` branching at the call site.
 */
data class BoardGameExtColors(
    val success: Color,
    val successBg: Color,
)

val LightExtColors = BoardGameExtColors(
    success = BoardGameColors.Success,
    successBg = BoardGameColors.SuccessBg,
)

val DarkExtColors = BoardGameExtColors(
    success = BoardGameDarkColors.Success,
    successBg = BoardGameDarkColors.SuccessBg,
)

val LocalBoardGameExtColors = staticCompositionLocalOf { LightExtColors }
