package com.goskar.boardgame.ui.gamesList.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.GameNetworkRepository
import com.goskar.boardgame.data.repository.HistoryGameNetworkRepository
import com.goskar.boardgame.data.repository.PlayerNetworkRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.time.LocalDate

data class GamePlayState(
    val game: Game? = null,
    val playerList: List<Player>? = emptyList(),
    val successAddPlayGame: Boolean = false,
    val successEditAllPlayer: Boolean = false,
    val successAddHistoryGame: Boolean = false,
    val errorVisible: Boolean = false,
    val winner: String = "Who Win?",
    val playDate: LocalDate = LocalDate.now(),
    val countSelectedPlayer: Int = 0
)

@KoinViewModel
class GamePlayViewModel(
    private val gameNetworkRepository: GameNetworkRepository,
    private val playerNetworkRepository: PlayerNetworkRepository,
    private val historyGameNetworkRepository: HistoryGameNetworkRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GamePlayState())
    val state = _state.asStateFlow()

    fun update(state: GamePlayState) {
        _state.update { state }
    }

    fun validateAllData() {
        viewModelScope.launch {
            validateAddHistoryGameData()
            validateEditGame()
            validateEditAllPlayer()
        }
    }

    private suspend fun validateAddHistoryGameData() {
        var listOfPlayer: List<String> = emptyList()
        state.value.playerList?.filter { it.selected }?.forEach { player ->
            listOfPlayer = listOfPlayer + player.name
        }


        val historyGame = HistoryGame(
            gameData = state.value.playDate.toString(),
            winner = state.value.winner,
            gameName = state.value.game?.name ?: "",
            listOfPlayer = listOfPlayer,
            description = ""
        )

        val response = historyGameNetworkRepository.addHistoryGame(historyGame = historyGame)

        when (response) {
            is RequestResult.Success -> {
                _state.update {
                    it.copy(
                        successAddHistoryGame = true,
                    )
                }
            }

            else -> {
                _state.update {
                    it.copy(
                        successAddHistoryGame = false,
                        errorVisible = true,
                    )
                }
            }
        }

    }

    private suspend fun validateEditGame() {
        val game = state.value.game?.copy(
            games = (state.value.game?.games ?: 0) + 1,
        )
        if (game != null) {
            val response = gameNetworkRepository.editGame(game)
            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successAddPlayGame = true
                        )
                    }
//                        validateEditAllPlayer()
                }

                else -> {
                    _state.update {
                        it.copy(
                            successAddPlayGame = false,
                            errorVisible = true
                        )
                    }
                }
            }
        } else {
            _state.update {
                it.copy(
                    successAddPlayGame = false,
                    errorVisible = true
                )
            }
        }
    }

    fun getAllPlayer() {
        viewModelScope.launch {
            val response = playerNetworkRepository.getAllPlayer()?.toMutableList()
            _state.update {
                it.copy(
                    playerList = response
                )
            }
        }
    }

    fun selectedPlayer(player: Player) {
        player.selected = !player.selected
        _state.update {
            it.copy(
                countSelectedPlayer = state.value.playerList?.filter { it.selected }?.size?:0
            )
        }
    }

    private suspend fun validateEditAllPlayer() {
        var item = 0
        state.value.playerList?.filter { it.selected }?.forEach { player ->

            val playerGames = player.copy(
                name = player.name,
                games = player.games + 1,
                winRatio = if (player.name == state.value.winner) player.winRatio + 1 else player.winRatio,
                description = player.description,
                selected = false
            )
            val response = playerNetworkRepository.editPlayer(playerGames)
            when (response) {
                is RequestResult.Success -> {
                    item++
                    if (item == state.value.playerList?.filter { it.selected }?.size) {
                        _state.update {
                            it.copy(
                                successEditAllPlayer = true,
                                errorVisible = false
                            )
                        }
                    }
                }

                else -> {
                    _state.update {
                        it.copy(
                            successEditAllPlayer = false,
                            errorVisible = true
                        )
                    }
                }
            }
        }
    }
}