package com.goskar.boardgame.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity
data class Game(
    val name: String,
    val expansion: Boolean,
    @ColumnInfo(name = "cooperate", defaultValue = "0")
    val cooperate: Boolean,
    val baseGame: String,
    val minPlayer: String,
    val maxPlayer: String,
    val uri: String? = null,
    val uriFromBgg: String? = null,
    val games: Int,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable