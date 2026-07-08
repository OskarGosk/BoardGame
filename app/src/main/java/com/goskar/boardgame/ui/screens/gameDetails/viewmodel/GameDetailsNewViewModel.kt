package com.goskar.boardgame.ui.screens.gameDetails.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.BoardGameBGG
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.bgg.BoardGameApiRepository
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

data class GameDetailsNewState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val name: String = "",
    val year: String = "",
    val imageUrl: String? = null,
    val description: String = "",
    val players: String = "",
    val bestPlayers: String = "",
    val playtime: String = "",
    val age: String = "",
    val rating: String = "",
    val weight: String = "",
    val cooperate: Boolean = false,
    val categories: List<String> = emptyList(),
    val mechanics: List<String> = emptyList(),
    val designers: List<String> = emptyList(),
    val publishers: List<String> = emptyList(),
    val alreadyInCollection: Boolean = false,
)

sealed interface GameDetailsNewEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : GameDetailsNewEvent
    data class Added(@StringRes val message: Int, val type: AppSnackBarType) : GameDetailsNewEvent
}

class GameDetailsNewViewModel(
    private val boardGameApiRepository: BoardGameApiRepository,
    private val gameDbRepository: GameDbRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GameDetailsNewState())
    val state = _state.asStateFlow()

    private val _events = Channel<GameDetailsNewEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var details: BoardGameBGG? = null
    private var gameName: String = ""
    private var loadedBggId: String = ""

    fun load(bggId: String, name: String) {
        gameName = name
        loadedBggId = bggId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false, name = name) }
            val data = (boardGameApiRepository.getGame(bggId) as? RequestResult.Success)
                ?.data?.boardGamesBGG?.firstOrNull()
            if (data == null) {
                _state.update { it.copy(isLoading = false, isError = true) }
                return@launch
            }
            details = data
            val owned = (gameDbRepository.getAllGame() as? RequestResult.Success)
                ?.data?.any { it.bggId == bggId } == true
            _state.update { data.toState(name).copy(alreadyInCollection = owned) }
        }
    }

    fun addToCollection() {
        val data = details ?: return
        viewModelScope.launch {
            val game = Game(
                name = gameName,
                expansion = false,
                cooperate = data.isCooperative(),
                baseGame = "",
                minPlayer = data.minPlayers?.toString() ?: "",
                maxPlayer = data.maxPlayers?.toString() ?: "",
                uriFromBgg = data.image ?: data.thumbnail,
                games = 0,
                category = data.categories?.firstOrNull()?.value,
                yearPublished = data.yearPublished,
                playTime = data.playingTime ?: data.maxPlayTime,
                rating = data.averageRating(),
                bggId = loadedBggId.ifBlank { null },
                id = UUID.randomUUID().toString(),
            )
            when (gameDbRepository.insertGame(game)) {
                is RequestResult.Success ->
                    _events.send(GameDetailsNewEvent.Added(R.string.success_global, AppSnackBarType.SUCCESS))

                is RequestResult.Error ->
                    _events.send(GameDetailsNewEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
            }
        }
    }

    private fun BoardGameBGG.toState(name: String) = GameDetailsNewState(
        isLoading = false,
        isError = false,
        name = name,
        year = yearPublished?.toString() ?: "",
        imageUrl = image ?: thumbnail,
        description = cleanDescription(description),
        players = playerRange(),
        bestPlayers = bestPlayers().orEmpty(),
        playtime = playtimeLabel(),
        age = age?.let { "$it+" } ?: "",
        rating = averageRating()?.let { String.format(Locale.US, "%.1f", it) } ?: "",
        weight = statistics?.ratings?.averageWeight?.let { String.format(Locale.US, "%.1f / 5", it) } ?: "",
        cooperate = isCooperative(),
        categories = categories?.mapNotNull { it.value } ?: emptyList(),
        mechanics = mechanics?.mapNotNull { it.value } ?: emptyList(),
        designers = designers?.mapNotNull { it.value } ?: emptyList(),
        publishers = publishers?.mapNotNull { it.value } ?: emptyList(),
    )

    private fun BoardGameBGG.playerRange(): String {
        val min = minPlayers
        val max = maxPlayers
        return when {
            min == null && max == null -> ""
            min == max -> "$min players"
            min != null && max != null -> "$min–$max players"
            else -> "${min ?: max} players"
        }
    }

    private fun BoardGameBGG.playtimeLabel(): String {
        val min = minPlayTime
        val max = maxPlayTime
        val single = playingTime
        return when {
            min != null && max != null && min != max -> "$min–$max min"
            single != null && single > 0 -> "$single min"
            max != null && max > 0 -> "$max min"
            else -> ""
        }
    }

    private fun cleanDescription(raw: String?): String =
        raw.orEmpty()
            .replace("&amp;", "&")
            .replace("&rsquo;", "'")
            .replace("&quot;", "\"")
            .replace("&mdash;", "—")
            .replace("&ndash;", "–")
            .replace("&#10;", "\n")
            .replace(Regex("&[a-zA-Z]+;"), "")
            .trim()
}
