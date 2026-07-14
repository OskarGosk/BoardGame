package com.goskar.boardgame.ui.screens.logGameplay.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

data class GameplayPlayer(
    val id: String,
    val name: String,
    val initials: String,
    val selected: Boolean = false,
    val score: String = "",
)

data class SessionVariant(
    val id: String,
    val name: String,
    val selected: Boolean = false,
)

data class GameOption(
    val id: String,
    val name: String,
)

data class AddGameplayNewState(
    val availableGames: List<GameOption> = emptyList(),
    val selectedGameId: String? = null,
    val selectedGame: String = "",
    val playDate: LocalDate = LocalDate.now(),
    val datePlayed: String = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
    val players: List<GameplayPlayer> = emptyList(),
    val variants: List<SessionVariant> = emptyList(),
    val winnerIndex: Int = -1,
    val notes: String = "",
    val durationMin: String = "",
    val cooperate: Boolean = false,
    val maxPlayers: Int? = null,
    val coopPlayersWon: Boolean? = null,
    val isEditMode: Boolean = false,
    val editSessionId: String? = null,
)

/** Winner label stored for cooperative games (players win together, or lose to the game). */
object CoopWinner {
    const val PLAYERS = "Players"
    const val COMPUTER = "Computer"
}

sealed interface AddGameplayEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : AddGameplayEvent
    data class Saved(@StringRes val message: Int, val type: AppSnackBarType) : AddGameplayEvent
}

class AddGameplayNewViewModel(
    private val playerDbRepository: PlayerDbRepository,
    private val gameDbRepository: GameDbRepository,
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddGameplayNewState())
    val state = _state.asStateFlow()

    private val _events = Channel<AddGameplayEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var allGames: List<Game> = emptyList()

    fun load(editSessionId: String? = null, preselectedGameId: String? = null) {
        viewModelScope.launch {
            allGames = getAllGameUseCase()
            val baseGames = allGames.filter { !it.expansion }

            val players = when (val response = playerDbRepository.getAllPlayer()) {
                is RequestResult.Success -> response.data.map { p ->
                    GameplayPlayer(id = p.id, name = p.name, initials = initialsOf(p.name))
                }

                is RequestResult.Error -> {
                    _events.send(AddGameplayEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                    emptyList()
                }
            }

            _state.update {
                it.copy(
                    availableGames = baseGames.map { g -> GameOption(g.id, g.name) },
                    players = players,
                )
            }

            if (editSessionId != null) {
                applyEditData(editSessionId)
            } else if (preselectedGameId != null) {
                selectGame(preselectedGameId)
            }
        }
    }

    private suspend fun applyEditData(editSessionId: String) {
        val history = (gamesHistoryDbRepository.getAllHistoryGame() as? RequestResult.Success)
            ?.data?.firstOrNull { it.id == editSessionId } ?: return
        val sessionExpansionIds = (gamesHistoryDbRepository.getAllHistoryGameExpansion() as? RequestResult.Success)
            ?.data?.filter { it.historyGameId == editSessionId }?.map { it.expansionId }?.toSet() ?: emptySet()

        val game = allGames.firstOrNull { !it.expansion && it.name == history.gameName }
        val variants = game?.let { g ->
            allGames.filter { it.expansion && it.baseGameId == g.id }
                .map { SessionVariant(id = it.id, name = it.name, selected = it.id in sessionExpansionIds) }
        } ?: emptyList()

        _state.update { s ->
            val markedPlayers = s.players.map { p ->
                p.copy(
                    selected = history.listOfPlayer.contains(p.name),
                    score = history.playerScores?.get(p.name)?.toString() ?: "",
                )
            }
            val coop = game?.cooperate == true
            s.copy(
                isEditMode = true,
                editSessionId = editSessionId,
                selectedGameId = game?.id,
                selectedGame = history.gameName,
                variants = variants,
                players = markedPlayers,
                cooperate = coop,
                maxPlayers = game?.maxPlayer?.toIntOrNull(),
                winnerIndex = if (coop) -1 else markedPlayers.indexOfFirst { it.name == history.winner },
                coopPlayersWon = if (coop) history.winner == CoopWinner.PLAYERS else null,
                notes = history.description,
                durationMin = history.durationMin?.toString() ?: "",
                playDate = history.gameData,
                datePlayed = history.gameData.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
            )
        }
    }

    fun selectGame(gameId: String) {
        val game = allGames.firstOrNull { it.id == gameId } ?: return
        val variants = allGames
            .filter { it.expansion && it.baseGameId == gameId }
            .map { SessionVariant(id = it.id, name = it.name) }
        _state.update {
            it.copy(
                selectedGameId = gameId,
                selectedGame = game.name,
                variants = variants,
                cooperate = game.cooperate,
                maxPlayers = game.maxPlayer.toIntOrNull(),
                // reset winner selection - it may no longer be valid for the new game
                winnerIndex = -1,
                coopPlayersWon = null,
            )
        }
    }

    fun selectCoopWin(playersWon: Boolean) = _state.update { it.copy(coopPlayersWon = playersWon) }

    fun updatePlayDate(date: LocalDate) = _state.update {
        it.copy(playDate = date, datePlayed = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
    }

    fun togglePlayer(index: Int) {
        val s = state.value
        val player = s.players.getOrNull(index) ?: return
        val selectedCount = s.players.count { it.selected }

        // block selecting more players than the game allows
        if (!player.selected && s.maxPlayers != null && selectedCount >= s.maxPlayers) {
            viewModelScope.launch {
                _events.send(AddGameplayEvent.ShowMessage(R.string.history_max_player_selected, AppSnackBarType.ERROR))
            }
            return
        }

        _state.update { st ->
            val newPlayers = st.players.mapIndexed { i, p ->
                if (i == index) p.copy(selected = !p.selected) else p
            }
            // if the winner is being deselected, clear the winner
            val newWinner = if (st.winnerIndex == index && player.selected) -1 else st.winnerIndex
            st.copy(players = newPlayers, winnerIndex = newWinner)
        }
    }

    fun toggleVariant(index: Int) = _state.update { s ->
        s.copy(variants = s.variants.mapIndexed { i, v ->
            if (i == index) v.copy(selected = !v.selected) else v
        })
    }

    fun selectWinner(index: Int) = _state.update { it.copy(winnerIndex = index) }

    fun updateNotes(value: String) = _state.update { it.copy(notes = value) }

    fun updateDuration(value: String) =
        _state.update { it.copy(durationMin = value.filter { c -> c.isDigit() }) }

    fun updatePlayerScore(index: Int, value: String) = _state.update { s ->
        s.copy(players = s.players.mapIndexed { i, p ->
            if (i == index) p.copy(score = value.filter { c -> c.isDigit() }) else p
        })
    }

    private data class WinnerResult(val name: String, val everyoneWins: Boolean)

    /** Resolves who won: for coop games it is Players/Computer; otherwise a participating player. */
    private fun resolveWinner(current: AddGameplayNewState): WinnerResult? =
        if (current.cooperate) {
            when (current.coopPlayersWon) {
                true -> WinnerResult(CoopWinner.PLAYERS, everyoneWins = true)
                false -> WinnerResult(CoopWinner.COMPUTER, everyoneWins = false)
                null -> null
            }
        } else {
            current.players.getOrNull(current.winnerIndex)
                ?.takeIf { it.selected }
                ?.let { WinnerResult(it.name, everyoneWins = false) }
        }

    fun logSession() {
        val current = state.value
        val gameName = current.selectedGame
        val selectedPlayers = current.players.filter { it.selected }

        if (gameName.isBlank() || selectedPlayers.isEmpty()) {
            emit(R.string.error_global)
            return
        }

        val winner = resolveWinner(current)
        if (winner == null) {
            emit(R.string.log_pick_winner)
            return
        }

        if (current.isEditMode) {
            updateSession(current, gameName, winner, selectedPlayers)
            return
        }

        val game = current.selectedGameId?.let { id -> allGames.firstOrNull { it.id == id } }
        if (game == null) {
            emit(R.string.error_global)
            return
        }

        val historyId = UUID.randomUUID().toString()

        viewModelScope.launch {
            val historyGame = HistoryGame(
                gameName = game.name,
                winner = winner.name,
                gameData = current.playDate,
                listOfPlayer = selectedPlayers.map { it.name },
                description = current.notes,
                durationMin = current.durationMin.toIntOrNull(),
                playerScores = scoresOf(selectedPlayers),
                id = historyId,
                baseGameId = game.baseGameId,
            )

            val saved = insertHistory(historyGame) &&
                insertExpansions(historyId, current.variants.filter { it.selected }) &&
                incrementGame(game) &&
                incrementExpansions(current.variants.filter { it.selected }) &&
                incrementPlayers(selectedPlayers, winner.name, winner.everyoneWins)

            if (saved) {
                _events.send(AddGameplayEvent.Saved(R.string.success_global, AppSnackBarType.SUCCESS))
            }
        }
    }

    private fun updateSession(
        current: AddGameplayNewState,
        gameName: String,
        winner: WinnerResult,
        selectedPlayers: List<GameplayPlayer>,
    ) {
        val id = current.editSessionId ?: return
        viewModelScope.launch {
            val historyGame = HistoryGame(
                gameName = gameName,
                winner = winner.name,
                gameData = current.playDate,
                listOfPlayer = selectedPlayers.map { it.name },
                description = current.notes,
                durationMin = current.durationMin.toIntOrNull(),
                playerScores = scoresOf(selectedPlayers),
                id = id,
            )
            val saved = handle(gamesHistoryDbRepository.editHistoryGame(historyGame)) &&
                handle(gamesHistoryDbRepository.deleteHistoryGameExpansionByHistoryId(id)) &&
                insertExpansions(id, current.variants.filter { it.selected })
            if (saved) {
                _events.send(AddGameplayEvent.Saved(R.string.success_global, AppSnackBarType.SUCCESS))
            }
        }
    }

    private fun emit(@StringRes message: Int) {
        viewModelScope.launch { _events.send(AddGameplayEvent.ShowMessage(message, AppSnackBarType.ERROR)) }
    }

    private suspend fun insertHistory(historyGame: HistoryGame): Boolean =
        handle(gamesHistoryDbRepository.insertHistoryGame(historyGame))

    private suspend fun insertExpansions(historyId: String, variants: List<SessionVariant>): Boolean {
        if (variants.isEmpty()) return true
        val expansions = variants.map {
            HistoryGameExpansion(
                historyGameId = historyId,
                expansionName = it.name,
                expansionId = it.id,
            )
        }
        return handle(gamesHistoryDbRepository.insertAllHistoryGameExpansion(expansions))
    }

    private suspend fun incrementGame(game: Game): Boolean =
        handle(gameDbRepository.editGame(game.copy(games = game.games + 1)))

    private suspend fun incrementExpansions(variants: List<SessionVariant>): Boolean {
        for (variant in variants) {
            val game = allGames.firstOrNull { it.id == variant.id } ?: continue
            if (!handle(gameDbRepository.editGame(game.copy(games = game.games + 1)))) return false
        }
        return true
    }

    private suspend fun incrementPlayers(
        players: List<GameplayPlayer>,
        winnerName: String,
        everyoneWins: Boolean,
    ): Boolean {
        val allPlayers = (playerDbRepository.getAllPlayer() as? RequestResult.Success)?.data ?: return false
        for (player in players) {
            val original = allPlayers.firstOrNull { it.id == player.id } ?: continue
            val won = everyoneWins || original.name == winnerName
            val updated = original.copy(
                games = original.games + 1,
                winRatio = if (won) original.winRatio + 1 else original.winRatio,
                selected = false,
            )
            if (!handle(playerDbRepository.editPlayer(updated))) return false
        }
        return true
    }

    private suspend fun handle(result: RequestResult<Boolean>): Boolean = when (result) {
        is RequestResult.Success -> true
        is RequestResult.Error -> {
            _events.send(AddGameplayEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
            false
        }
    }

    private fun scoresOf(players: List<GameplayPlayer>): Map<String, Int>? =
        players.filter { it.score.isNotBlank() }
            .associate { it.name to it.score.toInt() }
            .takeIf { it.isNotEmpty() }

    private fun initialsOf(name: String): String =
        name.trim().split(Regex("\\s+")).mapNotNull { it.firstOrNull() }.take(2)
            .joinToString("").ifBlank { name.take(2) }.uppercase()
}
