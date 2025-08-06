package com.goskar.boardgame.ui.gamesList.play.components

import androidx.annotation.StringRes
import com.goskar.boardgame.R

enum class GameVariantEnum(@StringRes var id: Int) {
//    Solo(R.string.history_solo),
    Coop(R.string.history_coop),
    Normal(R.string.history_normal)
}