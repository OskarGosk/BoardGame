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

data class GameDetailsState(
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
    val linkGameName: String? = null,
    val isLinking: Boolean = false,
)

sealed interface GameDetailsEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : GameDetailsEvent
    data class Added(@StringRes val message: Int, val type: AppSnackBarType) : GameDetailsEvent
    data class Linked(
        @StringRes val message: Int,
        val type: AppSnackBarType,
        val bggId: String,
        val gameName: String,
    ) : GameDetailsEvent
}

class GameDetailsViewModel(
    private val boardGameApiRepository: BoardGameApiRepository,
    private val gameDbRepository: GameDbRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GameDetailsState())
    val state = _state.asStateFlow()

    private val _events = Channel<GameDetailsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var details: BoardGameBGG? = null
    private var gameName: String = ""
    private var loadedBggId: String = ""
    private var linkGameId: String? = null

    /** With [linkGameId] the screen previews a BGG entry for linking to that collection game instead of adding it. */
    fun load(bggId: String, name: String, linkGameId: String? = null) {
        gameName = name
        loadedBggId = bggId
        this.linkGameId = linkGameId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false, name = name) }
            val data = (boardGameApiRepository.getGame(bggId) as? RequestResult.Success)
                ?.data?.boardGamesBGG?.firstOrNull()
            if (data == null) {
                _state.update { it.copy(isLoading = false, isError = true) }
                return@launch
            }
            details = data
            val games = (gameDbRepository.getAllGame() as? RequestResult.Success)?.data.orEmpty()
            _state.update {
                data.toState(name).copy(
                    alreadyInCollection = games.any { game -> game.bggId == bggId },
                    linkGameName = linkGameId?.let { id -> games.firstOrNull { game -> game.id == id }?.name },
                )
            }
        }
    }

    fun linkToGame() {
        val data = details ?: return
        val gameId = linkGameId ?: return
        if (_state.value.isLinking) return
        _state.update { it.copy(isLinking = true) }
        viewModelScope.launch {
            val game = (gameDbRepository.getAllGame() as? RequestResult.Success)
                ?.data?.firstOrNull { it.id == gameId }
            val result = game?.let { gameDbRepository.editGame(it.linkedTo(loadedBggId, data)) }
            _state.update { it.copy(isLinking = false) }
            if (game != null && result is RequestResult.Success) {
                _events.send(GameDetailsEvent.Linked(R.string.gd_linked, AppSnackBarType.SUCCESS, loadedBggId, game.name))
            } else {
                _events.send(GameDetailsEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
            }
        }
    }

    // Only empty fields are filled, so the user's name, own cover, modes and play count stay untouched.
    private fun Game.linkedTo(bggId: String, data: BoardGameBGG) = copy(
        bggId = bggId,
        minPlayer = minPlayer.ifBlank { data.minPlayers?.toString().orEmpty() },
        maxPlayer = maxPlayer.ifBlank { data.maxPlayers?.toString().orEmpty() },
        category = category?.takeIf { it.isNotBlank() } ?: data.categories?.firstOrNull()?.value,
        yearPublished = yearPublished ?: data.yearPublished,
        playTime = playTime ?: data.playingTime ?: data.maxPlayTime,
        rating = rating ?: data.averageRating(),
        uriFromBgg = if (uri.isNullOrBlank() && uriFromBgg.isNullOrBlank()) data.image ?: data.thumbnail else uriFromBgg,
    )

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
                    _events.send(GameDetailsEvent.Added(R.string.success_global, AppSnackBarType.SUCCESS))

                is RequestResult.Error ->
                    _events.send(GameDetailsEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
            }
        }
    }

    private fun BoardGameBGG.toState(name: String) = GameDetailsState(
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
            min == max -> "$min"
            min != null && max != null -> "$min–$max"
            else -> "${min ?: max}"
        }
    }

    private fun BoardGameBGG.playtimeLabel(): String {
        val min = minPlayTime
        val max = maxPlayTime
        val single = playingTime
        return when {
            min != null && max != null && min != max -> "$min–$max"
            single != null && single > 0 -> "$single"
            max != null && max > 0 -> "$max"
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
