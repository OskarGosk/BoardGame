package com.goskar.boardgame.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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
