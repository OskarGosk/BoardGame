package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class GetAllPlayerUseCase (
    private val playerDbRepository: PlayerDbRepository,
) {

    suspend operator fun invoke(): List<Player> =
        when (val response = playerDbRepository.getAllPlayer()) {
            is RequestResult.Success -> {
                response.data
            }
            is RequestResult.Error -> {
                emptyList()
            }
        }
}