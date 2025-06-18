package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class UpsertAllGameUseCase(
    private val gameDbRepository: GameDbRepository,
) {

    suspend operator fun invoke(allGame:List<Game>): Boolean =
        when (val response = gameDbRepository.insertAllGame(allGame)) {
            is RequestResult.Success -> {
                response.data
            }

            is RequestResult.Error -> {
                false
            }
        }

}