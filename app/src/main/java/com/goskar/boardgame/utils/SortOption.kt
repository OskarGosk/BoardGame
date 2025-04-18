package com.goskar.boardgame.utils

import androidx.annotation.StringRes
import com.goskar.boardgame.R

enum class SortList(@StringRes val value: Int) {
    DEFAULT(R.string.default_sort),
    A_Z(R.string.name_ascending),
    Z_A(R.string.name_descending),
    GAMES_MIN(R.string.played_ascending),
    GAMES_MAX(R.string.played_descending)
//    LOW_PRICE(R.string.lower_price, "price"),
//    HIGH_PRICE(R.string.higher_price, "-price");
}

enum class SortList2(@StringRes val value: Int) {
    DEFAULT(R.string.default_sort),
    A_Z(R.string.name_ascending),
    Z_A(R.string.name_descending),
//    GAMES_MIN(R.string.played_ascending),
//    GAMES_MAX(R.string.played_descending)
    LOW_PRICE(R.string.board_base),
    HIGH_PRICE(R.string.history_winner);
}