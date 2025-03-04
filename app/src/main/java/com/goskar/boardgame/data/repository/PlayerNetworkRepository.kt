package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.Player


interface PlayerNetworkRepository {
    suspend fun addPlayer(player: Player): RequestResult<Boolean>
    suspend fun getAllPlayer(): List<Player>?
    suspend fun deletePlayer(playerId: String): RequestResult<Boolean>
    suspend fun editPlayer(player: Player): RequestResult<Boolean>
}