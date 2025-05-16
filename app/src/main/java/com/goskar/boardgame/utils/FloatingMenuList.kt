package com.goskar.boardgame.utils

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

data class FloatingMenuList(
    val icon: ImageVector?,
    @StringRes val name: Int?,
    val screen: Screen
)