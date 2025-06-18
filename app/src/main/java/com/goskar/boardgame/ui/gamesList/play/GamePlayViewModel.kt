package com.goskar.boardgame.ui.gamesList.play

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.utils.CooperatePlayers
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
    val countSelectedPlayer: Int = 0,
    val descriptionGame: String = ""
)

@KoinViewModel
class GamePlayViewModel(
    private val playerDbRepository: PlayerDbRepository,
    private val gameDbRepository: GameDbRepository,
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GamePlayState())
    val state = _state.asStateFlow()

    fun update(state: GamePlayState) {
        _state.update { state }
    }

    fun validateAllData(context: Context) {
        viewModelScope.launch {
            validateAddHistoryGameData(context)
            validateEditGame()
            validateEditAllPlayer(context)
        }
    }

    private suspend fun validateAddHistoryGameData(context: Context) {
        var listOfPlayer: List<String> = emptyList()
        state.value.playerList?.filter { it.selected }?.forEach { player ->
            listOfPlayer = listOfPlayer + player.name
        }
        if (state.value.game?.cooperate == true) {
            listOfPlayer = listOf(context.resources.getString(CooperatePlayers.COMP.value)) + listOfPlayer
        }


        val historyGame = HistoryGame(
            gameData = state.value.playDate,
            winner = state.value.winner,
            gameName = state.value.game?.name ?: "",
            listOfPlayer = listOfPlayer,
            description = state.value.descriptionGame
        )

        val response = gamesHistoryDbRepository.insertHistoryGame(historyGame = historyGame)

        when (response) {
            is RequestResult.Success -> {
                _state.update {
                    it.copy(
                        successAddHistoryGame = true,
                    )
                }
            }

            is RequestResult.Error -> {
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
            val response = gameDbRepository.editGame(game)
            when (response) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successAddPlayGame = true
                        )
                    }
//                        validateEditAllPlayer()
                }

                is RequestResult.Error -> {
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
            when (val response = playerDbRepository.getAllPlayer()) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            playerList = response.data,
                            errorVisible = false
                        )
                    }
                }

                is RequestResult.Error -> {
                    _state.update {
                        it.copy(
                            errorVisible = true
                        )
                    }
                }
            }
        }
    }

    fun selectedPlayer(player: Player) {
        player.selected = !player.selected
        _state.update {
            it.copy(
                countSelectedPlayer = state.value.playerList?.filter { it.selected }?.size ?: 0
            )
        }
    }

    private suspend fun validateEditAllPlayer(context: Context) {
        var item = 0
        state.value.playerList?.filter { it.selected }?.forEach { player ->

            val playerGames = player.copy(
                name = player.name,
                games = player.games + 1,
                winRatio = if (player.name == state.value.winner || state.value.winner == context.resources.getString(CooperatePlayers.PLAYERS.value)) player.winRatio + 1 else player.winRatio,
                description = player.description,
                selected = false
            )
            val response = playerDbRepository.editPlayer(playerGames)
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

                is RequestResult.Error -> {
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