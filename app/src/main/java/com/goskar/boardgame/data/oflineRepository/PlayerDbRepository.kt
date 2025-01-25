package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.Player

interface PlayerDbRepository {

    suspend fun insertPlayer(player: Player) : Boolean

    suspend fun insertAllPlayer(playerList: List<Player>) : Boolean

    suspend fun getAllPlayer():List<Player>

    suspend fun deletePlayer(player: Player) : Boolean

    suspend fun editPlayer(player: Player) : Boolean
}