package com.goskar.boardgame.data.repository.firebase

import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.HistoryGameFirebase
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.rest.RequestResult

interface BoardGameFirebaseDataRepository {

    suspend fun getAllGame(): RequestResult<List<Game>>
    suspend fun getAllPlayer(): RequestResult<List<Player>>
    suspend fun getAllHistoryGame(): RequestResult<List<HistoryGameFirebase>>
    suspend fun getAllHistoryGameExpansion(): RequestResult<List<HistoryGameExpansion>>

    suspend fun uploadAll(data: CloudUserData): RequestResult<Boolean>

    suspend fun hasUserData(): RequestResult<Boolean>
    suspend fun getLegacyData(): RequestResult<CloudUserData>
}
