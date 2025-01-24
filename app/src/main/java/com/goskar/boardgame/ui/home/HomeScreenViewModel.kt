package com.goskar.boardgame.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.oflineRepository.GameDbRepository
import com.goskar.boardgame.data.oflineRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.oflineRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.GameNetworkRepository
import com.goskar.boardgame.data.repository.HistoryGameNetworkRepository
import com.goskar.boardgame.data.repository.PlayerNetworkRepository
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeScreenViewModel(
    private val gameNetworkRepository: GameNetworkRepository,
    private val playerNetworkRepository: PlayerNetworkRepository,
    private val historyGameNetworkRepository: HistoryGameNetworkRepository,
    private val playerDbRepository: PlayerDbRepository,
    private val gameDbRepository: GameDbRepository,
    private val gamesHistoryDbRepository: GamesHistoryDbRepository
) : ViewModel() {

    fun getAllData(){
        viewModelScope.launch {
            val response1 = playerNetworkRepository.getAllPlayer()
            playerDbRepository.insertAllPlayer(response1?: emptyList())

            val response2 = gameNetworkRepository.getAllGame()
            gameDbRepository.insertAllGame(response2)

            val response3 = historyGameNetworkRepository.getAll()
            gamesHistoryDbRepository.insertAllHistoryGame(response3)
        }
    }

}