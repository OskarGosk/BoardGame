package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.Player

interface PlayerDbRepository {

    suspend fun insertPlayer(player: Player)

    suspend fun insertAllPlayer(playerList: List<Player>)

    suspend fun getAllPlayer():List<Player>

    suspend fun deletePlayer(player: Player)

    suspend fun editPlayer(player: Player)
}