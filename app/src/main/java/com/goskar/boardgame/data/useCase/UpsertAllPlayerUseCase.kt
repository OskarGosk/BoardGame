package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class UpsertAllPlayerUseCase(
    private val playerDbRepository: PlayerDbRepository
) {

    suspend operator fun invoke(allPlayer:List<Player>): Boolean =
        when (val response = playerDbRepository.insertAllPlayer(allPlayer)) {
            is RequestResult.Success -> {
                response.data
            }

            is RequestResult.Error -> {
                false
            }
        }

}