package com.goskar.boardgame.ui.home.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


data class OtherBottomMenuList(
    @DrawableRes val icon: Int,
    @StringRes val name: Int,
    val onClick: () -> Unit = {},
)