package com.goskar.boardgame.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity
data class Game(
    val name: String,
    val expansion: Boolean,
    val baseGame: String,
    val minPlayer: String,
    val maxPlayer: String,
    val games: Int,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable