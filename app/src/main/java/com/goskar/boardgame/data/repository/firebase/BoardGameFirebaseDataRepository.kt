package com.goskar.boardgame.data.repository.firebase

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGameFirebase
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.rest.RequestResult

interface BoardGameFirebaseDataRepository {

    suspend fun getAllGame(): RequestResult<List<Game>>
    suspend fun addAllGame(game: Map<String, Game>): RequestResult<Boolean>


    suspend fun getAllPlayer(): RequestResult<List<Player>>
    suspend fun addPlayer(player: Map<String,Player>): RequestResult<Boolean>

    suspend fun getAllHistoryGame(): RequestResult<List<HistoryGameFirebase>>
    suspend fun addHistoryGame(historyGame: Map<String, HistoryGameFirebase>): RequestResult<Boolean>
}