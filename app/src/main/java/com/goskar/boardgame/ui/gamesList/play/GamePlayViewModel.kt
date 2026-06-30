package com.goskar.boardgame.ui.gamesList.play

import android.content.Context
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
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
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.utils.CooperatePlayers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

sealed interface GamePlayEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : GamePlayEvent
    data class Saved(@StringRes val message: Int) : GamePlayEvent
}

data class GamePlayState(
    val game: Game? = null,
    val gameList: List<ExpansionGameUiState>? = emptyList(),
    val playerList: List<Player>? = emptyList(),
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

class GamePlayViewModel(
    private val playerDbRepository: PlayerDbRepository,
    private val gameDbRepository: GameDbRepository,
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GamePlayState())
    val state = _state.asStateFlow()

    private val _events = Channel<GamePlayEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun updateGame(game: Game) {
        _state.update {
            it.copy(
                game = game
            )
        }
    }

    /** Test seam: [gameList] is normally populated by [getAllGame]. */
    @VisibleForTesting
    fun updateGameList(list: List<ExpansionGameUiState>) {
        _state.update { it.copy(gameList = list) }
    }

    fun updateSearchTxt(value: String) {
        _state.update { it.copy(searchTxt = value) }
    }

    fun updateSortOption(value: Int) {
        _state.update { it.copy(sortOption = value) }
    }

    fun updatePlayDate(date: LocalDate) {
        _state.update {
            it.copy(
                playDate = date
            )
        }
    }

    fun updateWinner(winner: String) {
        _state.update {
            it.copy(
                winner = winner
            )
        }
    }

    fun updateGameVariantAndWinner(gameVariant: Int, winner: String) {
        _state.update {
            it.copy(
                gameVariant = gameVariant,
                winner = winner
            )
        }
    }

    fun updateDescription(description: String) {
        _state.update {
            it.copy(
                descriptionGame = description
            )
        }
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
            val saved = validateAddHistoryGameData() &&
                    validateEditAllExpansion() &&
                    validateEditGame() &&
                    validateEditAllPlayer(context)
            if (saved) {
                _events.send(GamePlayEvent.Saved(R.string.success_global))
            }
        }
    }

    private suspend fun validateAddHistoryGameData(): Boolean {
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

        return when (gamesHistoryDbRepository.insertHistoryGame(historyGame = historyGame)) {
            is RequestResult.Success -> validateAddHistoryGameExpansionData()
            is RequestResult.Error -> {
                _events.send(
                    GamePlayEvent.ShowMessage(
                        R.string.error_global,
                        AppSnackBarType.ERROR
                    )
                )
                false
            }
        }
    }

    private suspend fun validateAddHistoryGameExpansionData(): Boolean {
        var expansionHistoryList: List<HistoryGameExpansion> = emptyList()

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

        return when (gamesHistoryDbRepository.insertAllHistoryGameExpansion(expansionHistoryList)) {
            is RequestResult.Success -> true
            is RequestResult.Error -> {
                _events.send(
                    GamePlayEvent.ShowMessage(
                        R.string.error_global,
                        AppSnackBarType.ERROR
                    )
                )
                false
            }
        }
    }

    private suspend fun validateEditGame(): Boolean {
        val game = state.value.game?.copy(
            games = (state.value.game?.games ?: 0) + 1,
        )
        if (game == null) {
            _events.send(GamePlayEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
            return false
        }
        return when (gameDbRepository.editGame(game)) {
            is RequestResult.Success -> true
            is RequestResult.Error -> {
                _events.send(
                    GamePlayEvent.ShowMessage(
                        R.string.error_global,
                        AppSnackBarType.ERROR
                    )
                )
                false
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
                        )
                    }
                }

                is RequestResult.Error -> {
                    _events.send(
                        GamePlayEvent.ShowMessage(
                            R.string.error_global,
                            AppSnackBarType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun selectedPlayer(player: Player) {
        val newPlayerList = state.value.playerList?.map {
            if (it.id == player.id) it.copy(selected = !it.selected) else it
        }
        _state.update {
            it.copy(
                playerList = newPlayerList,
                countSelectedPlayer = newPlayerList?.count { p -> p.selected } ?: 0
            )
        }
    }

    private suspend fun validateEditAllPlayer(context: Context): Boolean {
        val selectedPlayers = state.value.playerList?.filter { it.selected } ?: emptyList()
        for (player in selectedPlayers) {
            val playerGames = player.copy(
                games = player.games + 1,
                winRatio = if (player.name == state.value.winner || state.value.winner == context.resources.getString(
                        CooperatePlayers.PLAYERS.value
                    )
                ) player.winRatio + 1 else player.winRatio,
                selected = false
            )
            when (playerDbRepository.editPlayer(playerGames)) {
                is RequestResult.Success -> Unit
                is RequestResult.Error -> {
                    _events.send(
                        GamePlayEvent.ShowMessage(
                            R.string.error_global,
                            AppSnackBarType.ERROR
                        )
                    )
                    return false
                }
            }
        }
        return true
    }

    private suspend fun validateEditAllExpansion(): Boolean {
        val selectedExpansions = state.value.gameList?.filter { it.isSelected } ?: emptyList()
        for (expansion in selectedExpansions) {
            val game = expansion.game.copy(
                games = (expansion.game.games ?: 0) + 1,
            )
            when (gameDbRepository.editGame(game)) {
                is RequestResult.Success -> Unit
                is RequestResult.Error -> {
                    _events.send(
                        GamePlayEvent.ShowMessage(
                            R.string.error_global,
                            AppSnackBarType.ERROR
                        )
                    )
                    return false
                }
            }
        }
        return true
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