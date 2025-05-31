package com.goskar.boardgame.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User (
    @PrimaryKey
    val id: Long = 0,
    val email: String? = null,
    val token: String? = null,
    val userUID: String? = null
) : Serializable