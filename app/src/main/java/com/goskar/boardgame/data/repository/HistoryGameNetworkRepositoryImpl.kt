package com.goskar.boardgame.data.repository

import com.goskar.boardgame.data.rest.ApiBoardGame
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.HistoryGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class HistoryGameNetworkRepositoryImpl(
    private val apiBoardGame : ApiBoardGame,
): HistoryGameNetworkRepository {

    override suspend fun addHistoryGame(historyGame: HistoryGame): RequestResult<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = apiBoardGame.addHistory(historyGame)
                when {
                    response.isSuccessful ->{
                        RequestResult.Success(true)
                    }
                    else -> throw IllegalStateException()
                }
            }.onFailure {
                Timber.tag("Add game").e("Can't add player\n  ${it.stackTraceToString()}")
            }
        }.fold(onSuccess = {it}, onFailure = { RequestResult.Error(it)})
    }

    override suspend fun getAll(): List<HistoryGame>{
        return apiBoardGame.getAllHistoryGame().map { element ->
            element.value.copy(id = element.key)
        }
    }

    override suspend fun deleteHistoryGame (historyGameId: String){
        apiBoardGame.deleteHistoryGame(historyGameId)
    }

    override suspend fun editHistoryGame (historyGame: HistoryGame) {
        apiBoardGame.editHistoryGame(historyGame.id, historyGame)
    }
}