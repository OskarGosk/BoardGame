package com.goskar.boardgame.ui.gamesList.play

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.utils.CooperatePlayers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.time.LocalDate
import java.util.UUID

data class GamePlayState(
    val game: Game? = null,
    val gameList: List<ExpansionGameUiState>? = emptyList(),
    val playerList: List<Player>? = emptyList(),
    val successAddPlayGame: Boolean = false,
    val successEditAllPlayer: Boolean = false,
    val successAddHistoryGame: Boolean = false,
    val errorVisible: Boolean = false,
    val winner: String = "Who Win?",
    val gameVariant: Int = R.string.history_normal,
    val playDate: LocalDate = LocalDate.now(),
    val countSelectedPlayer: Int = 0,
    val descriptionGame: String = "",
    val searchTxt: String = "",
    val sortOption: Int = R.string.default_sort,
    val id: String = UUID.randomUUID().toString()
)

data class ExpansionGameUiState(
    val game: Game,
    val isSelected: Boolean = false
)

@KoinViewModel
class GamePlayViewModel(
    private val playerDbRepository: PlayerDbRepository,
    private val gameDbRepository: GameDbRepository,
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GamePlayState())
    val state = _state.asStateFlow()

    fun update(state: GamePlayState) {
        _state.update { state }
    }

    private fun setGameData() {
        if (state.value.game?.expansion == true) {
            val newExpansionGameList = state.value.gameList?.filter {
                it.game.baseGameId == state.value.game?.baseGameId
            }

            val tempExpansionId = state.value.game?.id


            val game = state.value.gameList?.firstOrNull {
                it.game.id == state.value.game?.baseGameId
            }?.game

            _state.update {
                it.copy(
                    game = game,
                    gameList = newExpansionGameList
                )
            }
            tempExpansionId?.let {
                selectExpansion(it)
            }
        } else {
            val newExpansionGameList = state.value.gameList?.filter {
                it.game.baseGameId == state.value.game?.id
            }
            _state.update {
                it.copy(
                    gameList = newExpansionGameList
                )
            }
        }
    }

    fun validateAllData(context: Context) {
        viewModelScope.launch {
            validateAddHistoryGameData(context)
            validateEditAllExpansion()
            validateEditGame()
            validateEditAllPlayer(context)
        }
    }

    private suspend fun validateAddHistoryGameData(context: Context) {
        var listOfPlayer: List<String> = emptyList()
        state.value.playerList?.filter { it.selected }?.forEach { player ->
            listOfPlayer = listOfPlayer + player.name
        }


        val historyGame = HistoryGame(
            gameData = state.value.playDate,
            winner = state.value.winner,
            gameName = state.value.game?.name ?: "",
            listOfPlayer = listOfPlayer,
            description = state.value.descriptionGame,
            id = state.value.id
        )

        val response = gamesHistoryDbRepository.insertHistoryGame(historyGame = historyGame)

        when (response) {
            is RequestResult.Success -> {
                validateAddHistoryGameExpansionData()
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

    private suspend fun validateAddHistoryGameExpansionData() {
        var expansionHistoryList : List<HistoryGameExpansion> = emptyList()

        state.value.gameList?.forEach {
            if (it.isSelected) {
                val expansionGameData = HistoryGameExpansion(
                    historyGameId = state.value.id,
                    expansionName = it.game.name,
                    expansionId = it.game.id
                )

                expansionHistoryList = expansionHistoryList + expansionGameData
            }
        }

        val response = gamesHistoryDbRepository.insertAllHistoryGameExpansion(expansionHistoryList)

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

    fun setGameVariant() {
        _state.update {
            it.copy(
                gameVariant = when (state.value.game?.cooperate) {
                    true -> R.string.history_coop
                    else -> R.string.history_normal
                }
            )
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
                winRatio = if (player.name == state.value.winner || state.value.winner == context.resources.getString(
                        CooperatePlayers.PLAYERS.value
                    )
                ) player.winRatio + 1 else player.winRatio,
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

    private suspend fun validateEditAllExpansion() {
        state.value.gameList?.filter { it.isSelected }?.forEach { expansion ->

            val game = expansion.game.copy(
                games = (expansion.game.games ?: 0) + 1,
            )
            when (val response = gameDbRepository.editGame(game)) {
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
//                            successAddPlayGame = true
                        )
                    }
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
        }
    }

    fun getAllGame() {
        viewModelScope.launch {
            val games = getAllGameUseCase.invoke()
            val gameUiStates = games.map { ExpansionGameUiState(game = it) }
            _state.update {
                it.copy(
                    gameList = gameUiStates
                )
            }
            setGameData()
        }
    }

    fun selectExpansion(expansionId: String) {
        val newGameList = state.value.gameList?.map {
            if (it.game.id == expansionId) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        } ?: emptyList()
        _state.update {
            it.copy(
                gameList = newGameList
            )
        }
    }
}