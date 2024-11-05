package com.goskar.boardgame.data.repository

import OGosk.boardgamebase.model.Player
import OGosk.boardgamebase.model.PlayerIdRespons

interface PlayerNetworkRepository {

    suspend fun addPlayer(player: Player): PlayerIdRespons
    suspend fun getAllPlayer(): List<Player>
    suspend fun deletePlayer(playerId: String)
    suspend fun editPlayer(player: Player)

}