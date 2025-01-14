package com.goskar.boardgame.ui.games.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.GameNetworkRepository
import com.goskar.boardgame.data.repository.PlayerNetworkRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GamePLayState(
    val game: Game? = null,
    val playerList: List<Player>? = emptyList(),
    val successAddPlayGame: Boolean = false,
    val successEditAllPlayer: Boolean = false,
    val errorVisible: Boolean = false,
    val winner: String = "Who Win?"
)

@KoinViewModel
class GamePlayViewModel(
    private val gameNetworkRepository: GameNetworkRepository,
    private val playerNetworkRepository: PlayerNetworkRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GamePLayState())
    val state = _state.asStateFlow()

    fun update(state: GamePLayState) {
        _state.update { state }
    }

    fun validateEditGame() {
        viewModelScope.launch {
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
                        validateEditAllPlayer()
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
    }

    fun validateEditAllPlayer() {
        viewModelScope.launch {
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
}