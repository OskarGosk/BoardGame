package com.goskar.boardgame.ui.components

import androidx.annotation.StringRes
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.GameMode

@get:StringRes
val GameMode.labelRes: Int
    get() = when (this) {
        GameMode.COMPETITIVE -> R.string.mode_competitive
        GameMode.COOPERATIVE -> R.string.mode_cooperative
        GameMode.TEAM -> R.string.mode_team
    }
