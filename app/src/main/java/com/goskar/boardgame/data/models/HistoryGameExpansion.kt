package com.goskar.boardgame.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity
data class HistoryGameExpansion(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val historyGameId: String,
    val expansionName: String,
    val expansionId: String
): Serializable