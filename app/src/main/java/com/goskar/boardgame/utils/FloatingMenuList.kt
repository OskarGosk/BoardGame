package com.goskar.boardgame.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

data class FloatingMenuList(
    @DrawableRes val icon: Int?,
    @StringRes val name: Int?,
    val screen: Screen
)