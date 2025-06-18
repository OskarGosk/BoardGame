package com.goskar.boardgame.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.util.UUID

@Entity
data class HistoryGame(
    val gameName: String,
    val winner: String,
    val gameData: LocalDate,
    val listOfPlayer: List<String>,
    val description: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable

data class HistoryGameFirebase(
    val gameName: String,
    val winner: String,
    val gameData: String,
    val listOfPlayer: List<String>,
    val description: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable