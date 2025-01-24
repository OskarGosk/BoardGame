package com.goskar.boardgame.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity
data class Player(
    val name: String,
//    val image: Image,
    val games: Int,
    val winRatio: Int,
    val description: String,
    var selected: Boolean,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable