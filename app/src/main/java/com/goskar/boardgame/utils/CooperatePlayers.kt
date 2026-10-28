package com.goskar.boardgame.utils

import androidx.annotation.StringRes
import com.goskar.boardgame.R

enum class CooperatePlayers(@StringRes val value: Int) {
    COMP(R.string.history_comp),
    PLAYERS(R.string.history_players),
}
