package com.goskar.boardgame.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class HistoryGameExpansion(
    @PrimaryKey(autoGenerate = true) val Id: Long = 0,
    val historyGameId: String,
    val expansionName: String,
    val expansionId: String
): Serializable