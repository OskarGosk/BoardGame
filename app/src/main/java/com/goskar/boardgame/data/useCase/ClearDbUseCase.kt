package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult

class ClearDbUseCase(
    private val gameDbRepository: GameDbRepository,
    private val playerDbRepository: PlayerDbRepository,
    private val historyDbRepository: GamesHistoryDbRepository,
) {
    suspend operator fun invoke(): Boolean {
        return attemptToDeleteAllData(
            gameDbRepository::deleteAllGame,
            playerDbRepository::deleteAllPlayer,
            historyDbRepository::deleteAllHistory,
            historyDbRepository::deleteAllHistoryExpansion
        )
    }

    private suspend fun attemptToDeleteAllData(
        firstAction: suspend () -> RequestResult<Boolean>,
        secondAction: suspend () -> RequestResult<Boolean>,
        thirdAction: suspend () -> RequestResult<Boolean>,
        fourthAction: suspend () -> RequestResult<Boolean>,
        ): Boolean {
        if (firstAction() is RequestResult.Success) {
            if (secondAction() is RequestResult.Success) {
                if (thirdAction() is RequestResult.Success) {
                    if (fourthAction() is RequestResult.Success) {
                        return true
                    } else return false
                } else return false
            } else return false
        } else return false
    }
}