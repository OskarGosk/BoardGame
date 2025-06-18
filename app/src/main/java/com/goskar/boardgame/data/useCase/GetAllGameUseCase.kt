package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class GetAllGameUseCase(
    private val gameDbRepository: GameDbRepository,
) {

    suspend operator fun invoke(): List<Game> =
        when (val response = gameDbRepository.getAllGame()) {
            is RequestResult.Success -> {
                response.data
            }
            is RequestResult.Error -> {
                emptyList<Game>()
            }
        }

}