package com.goskar.boardgame.data.repository

import OGosk.boardgamebase.model.Player
import OGosk.boardgamebase.model.PlayerIdRespons
import com.goskar.boardgame.data.rest.ApiBoardGame

class PlayerNetworkRepositoryImpl(
    private val apiBoardGame: ApiBoardGame,
) :PlayerNetworkRepository{

    override suspend fun addPlayer(player: Player): PlayerIdRespons {
        return apiBoardGame.addPlayer(player)
    }

    override suspend fun getAllPlayer(): List<Player> {
        return apiBoardGame.getAllPlayer().map { element ->
            element.value.copy(id = element.key)
        }
    }

    override suspend fun deletePlayer (playerId: String){
        apiBoardGame.deletePlayer(playerId)
    }

    override suspend fun editPlayer (player: Player){
        apiBoardGame.editPlayer(player.id, player)
    }
}