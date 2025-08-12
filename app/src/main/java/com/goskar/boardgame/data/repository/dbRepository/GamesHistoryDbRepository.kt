package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.rest.RequestResult

interface GamesHistoryDbRepository {
    suspend fun insertHistoryGame(historyGame: HistoryGame) : RequestResult<Boolean>

    suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>) : RequestResult<Boolean>

    suspend fun getAllHistoryGame(): RequestResult<List<HistoryGame>>

    suspend fun deleteHistoryGame(historyGame: HistoryGame) : RequestResult<Boolean>

    suspend fun editHistoryGame(historyGame: HistoryGame) : RequestResult<Boolean>

    suspend fun deleteAllHistory():RequestResult<Boolean>

    suspend fun insertHistoryGameExpansion(expansion: HistoryGameExpansion) : RequestResult<Boolean>

    suspend fun insertAllHistoryGameExpansion(expansion: List<HistoryGameExpansion>): RequestResult<Boolean>

}