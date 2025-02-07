package com.goskar.boardgame.data.oflineRepository

import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.rest.RequestResult

interface PlayerDbRepository {

    suspend fun insertPlayer(player: Player): RequestResult<Boolean>

    suspend fun insertAllPlayer(playerList: List<Player>): RequestResult<Boolean>

    suspend fun getAllPlayer(): RequestResult<Boolean>

    suspend fun deletePlayer(player: Player): RequestResult<Boolean>

    suspend fun editPlayer(player: Player): RequestResult<Boolean>
}