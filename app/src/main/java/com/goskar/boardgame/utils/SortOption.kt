package com.goskar.boardgame.utils

import androidx.annotation.StringRes
import com.goskar.boardgame.R

enum class SortList(@StringRes val value: Int, val sort: String) {
    DEFAULT(R.string.default_sort, ""),
    A_Z(R.string.ascending, "name"),
    Z_A(R.string.descending, "-name");
//    LOW_PRICE(R.string.lower_price, "price"),
//    HIGH_PRICE(R.string.higher_price, "-price");

    companion object {
        fun getSortByValue(value: Int): String? {
            return entries.find { it.value == value }?.sort
        }
    }
}