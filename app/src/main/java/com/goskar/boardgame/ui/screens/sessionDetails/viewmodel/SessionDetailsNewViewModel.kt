package com.goskar.boardgame.ui.screens.sessionDetails.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.components.AppChipStyle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class SessionPlayerResult(
    val name: String,
    val initials: String,
    val score: String,
    val isWinner: Boolean = false,
)

data class SessionDetailsNewState(
    val sessionId: String? = null,
    val gameName: String = "Dune: Imperium",
    val category: String = "Strategy",
    val categoryStyle: AppChipStyle = AppChipStyle.CATEGORY,
    val dateLabel: String = "Oct 27, 2023",
    val duration: String = "—",
    val playerCount: String = "4",
    val players: List<SessionPlayerResult> = listOf(
        SessionPlayerResult("M. Kane", "MK", "", isWinner = true),
        SessionPlayerResult("Alex M.", "AM", ""),
        SessionPlayerResult("Sarah K.", "SK", ""),
        SessionPlayerResult("Jordan T.", "JT", ""),
    ),
    val variants: List<String> = listOf("Rise of Ix Expansion", "Tournament Rules"),
    val notes: String = "Tight game that came down to the final round — M. Kane secured the win with a last-turn Imperium play.",
)

sealed interface SessionDetailsEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : SessionDetailsEvent
    data object Deleted : SessionDetailsEvent
}

class SessionDetailsNewViewModel(
    private val gamesHistoryDbRepository: GamesHistoryDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SessionDetailsNewState())
    val state = _state.asStateFlow()

    private val _events = Channel<SessionDetailsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var loadedId: String? = null

    fun load(historyGameId: String) {
        loadedId = historyGameId
        viewModelScope.launch {
            val history = (gamesHistoryDbRepository.getAllHistoryGame() as? RequestResult.Success)
                ?.data?.firstOrNull { it.id == historyGameId } ?: return@launch
            val expansions = (gamesHistoryDbRepository.getAllHistoryGameExpansion() as? RequestResult.Success)
                ?.data?.filter { it.historyGameId == historyGameId } ?: emptyList()
            val category = getAllGameUseCase().firstOrNull { it.name == history.gameName }?.category

            _state.update {
                it.copy(
                    sessionId = history.id,
                    gameName = history.gameName,
                    category = category ?: "",
                    dateLabel = history.gameData.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    duration = history.durationMin?.let { "${it}m" } ?: "—",
                    playerCount = history.listOfPlayer.size.toString(),
                    players = history.listOfPlayer.map { name ->
                        SessionPlayerResult(
                            name = name,
                            initials = initialsOf(name),
                            score = history.playerScores?.get(name)?.let { "$it pts" } ?: "",
                            isWinner = name.equals(history.winner, ignoreCase = true),
                        )
                    },
                    variants = expansions.map { exp -> exp.expansionName },
                    notes = history.description,
                )
            }
        }
    }

    fun delete() {
        val id = loadedId ?: return
        viewModelScope.launch {
            val history = (gamesHistoryDbRepository.getAllHistoryGame() as? RequestResult.Success)
                ?.data?.firstOrNull { it.id == id }
            if (history == null) {
                _events.send(SessionDetailsEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
                return@launch
            }
            when (gamesHistoryDbRepository.deleteHistoryGame(history)) {
                is RequestResult.Success -> {
                    gamesHistoryDbRepository.deleteHistoryGameExpansionByHistoryId(id)
                    _events.send(SessionDetailsEvent.Deleted)
                }
                is RequestResult.Error -> _events.send(
                    SessionDetailsEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR)
                )
            }
        }
    }

    private fun initialsOf(name: String): String =
        name.trim().split(Regex("\\s+")).mapNotNull { it.firstOrNull() }.take(2)
            .joinToString("").ifBlank { name.take(2) }.uppercase()
}
