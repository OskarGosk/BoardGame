package com.goskar.boardgame.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import com.goskar.boardgame.utils.convertHistoryGameListToDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

data class RecentSession(
    val gameName: String,
    val date: String,
    val playersInitials: List<String>,
    val winner: String,
)

data class HomeNewState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val greeting: String = "WELCOME,",
    val totalGames: String = "0",
    val mostPlayed: String = "—",
    val winRatio: String = "0%",
    val winRatioProgress: Float = 0f,
    val recentSessions: List<RecentSession> = emptyList(),
)

class HomeNewViewModel(
    private val getAllGameUseCase: GetAllGameUseCase,
    private val historyRepository: GamesHistoryDbRepository,
    private val userSession: UserRepository,
    private val api: BoardGameFirebaseDataRepository,
    private val addAllGameToDb: UpsertAllGameUseCase,
    private val addAllPlayerToDb: UpsertAllPlayerUseCase,
    private val addAllHistoryToDb: UpsertAllHistoryGameUseCase,
    private val addAllHistoryGameExpansionToDb: UpsertAllHistoryGameExpansionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeNewState())
    val state = _state.asStateFlow()

    fun load(firstLogin: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            if (firstLogin) downloadFromCloud()
            computeStats()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun computeStats() {
        val games = getAllGameUseCase()
        val history = (historyRepository.getAllHistoryGame() as? RequestResult.Success)?.data ?: emptyList()
        val user = userSession.getCurrentSession()

        val name = when {
            user?.userUID == "guest" -> "Guest"
            !user?.email.isNullOrBlank() -> user!!.email!!.substringBefore("@")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            else -> "Player"
        }

        val mostPlayed = games.maxByOrNull { it.games }?.name ?: "—"

        val wins = history.count { it.winner.trim().equals(name, ignoreCase = true) }
        val ratio = if (history.isNotEmpty()) wins.toFloat() / history.size else 0f

        val recent = history
            .sortedByDescending { it.gameData }
            .take(3)
            .map { h ->
                RecentSession(
                    gameName = h.gameName,
                    date = h.gameData.year.toString(),
                    playersInitials = h.listOfPlayer.map { initialsOf(it) },
                    winner = "Winner: ${h.winner}",
                )
            }

        _state.update {
            it.copy(
                userName = name,
                greeting = timeGreeting(),
                totalGames = games.size.toString(),
                mostPlayed = mostPlayed,
                winRatio = "${(ratio * 100).toInt()}%",
                winRatioProgress = ratio,
                recentSessions = recent,
            )
        }
    }

    private fun initialsOf(name: String): String =
        name.trim().split(Regex("\\s+"))
            .mapNotNull { it.firstOrNull() }
            .take(2)
            .joinToString("")
            .ifBlank { name.take(2) }
            .uppercase()

    private fun timeGreeting(): String = when (LocalTime.now().hour) {
        in 5..11 -> "GOOD MORNING,"
        in 12..17 -> "GOOD AFTERNOON,"
        else -> "GOOD EVENING,"
    }


    private suspend fun downloadFromCloud() {
        (api.getAllGame() as? RequestResult.Success)?.let { addAllGameToDb.invoke(it.data) }
        (api.getAllPlayer() as? RequestResult.Success)?.let { addAllPlayerToDb.invoke(it.data) }
        (api.getAllHistoryGame() as? RequestResult.Success)?.let { addAllHistoryToDb.invoke(convertHistoryGameListToDto(it.data)) }
        (api.getAllHistoryGameExpansion() as? RequestResult.Success)?.let { addAllHistoryGameExpansionToDb.invoke(it.data) }
    }
}
