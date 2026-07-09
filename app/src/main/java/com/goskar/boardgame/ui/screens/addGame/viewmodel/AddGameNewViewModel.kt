package com.goskar.boardgame.ui.screens.addGame.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.bgg.BoardGameApiRepository
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class PopularMatch(
    val id: String = "",
    val name: String,
    val category: String,
    val year: String,
)

data class AddGameNewState(
    val query: String = "",
    val selectedTab: Int = 0,
    val isSearching: Boolean = false,
    val popularMatches: List<PopularMatch> = emptyList(),
    val name: String = "",
    val minPlayers: String = "",
    val maxPlayers: String = "",
    val cooperate: Boolean = false,
    val expansion: Boolean = false,
    val baseGame: String = "",
    val baseGameOptions: List<String> = emptyList(),
    val category: String = "",
    val year: String = "",
    val coverUri: String? = null,
    val hasCover: Boolean = false,
    val isEditMode: Boolean = false,
    val editGameId: String? = null,
)

sealed interface AddGameEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : AddGameEvent
    data class Saved(@StringRes val message: Int, val type: AppSnackBarType) : AddGameEvent
}

class AddGameNewViewModel(
    private val gameDbRepository: GameDbRepository,
    private val getAllGameUseCase: GetAllGameUseCase,
    private val boardGameApiRepository: BoardGameApiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddGameNewState())
    val state = _state.asStateFlow()

    private val _events = Channel<AddGameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var baseGames: List<Game> = emptyList()
    private var editingGame: Game? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            baseGames = getAllGameUseCase().filter { !it.expansion }
            _state.update { it.copy(baseGameOptions = baseGames.map { game -> game.name }) }
        }
    }

    fun loadForEdit(gameId: String) {
        viewModelScope.launch {
            val all = getAllGameUseCase()
            baseGames = all.filter { !it.expansion }
            val game = all.firstOrNull { it.id == gameId } ?: return@launch
            editingGame = game
            _state.update {
                it.copy(
                    isEditMode = true,
                    editGameId = game.id,
                    selectedTab = 1, // Manual form only
                    baseGameOptions = baseGames.map { g -> g.name },
                    name = game.name,
                    minPlayers = game.minPlayer,
                    maxPlayers = game.maxPlayer,
                    cooperate = game.cooperate,
                    expansion = game.expansion,
                    baseGame = game.baseGame,
                    category = game.category.orEmpty(),
                    year = game.yearPublished?.toString().orEmpty(),
                    coverUri = game.uri,
                    hasCover = !game.uri.isNullOrBlank(),
                )
            }
        }
    }

    fun updateQuery(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun search() {
        val query = state.value.query.trim()
        searchJob?.cancel()
        if (query.length < 2) {
            _state.update { it.copy(popularMatches = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            searchBgg(query)
        }
    }

    fun selectTab(index: Int) = _state.update { it.copy(selectedTab = index) }

    fun updateName(value: String) = _state.update { it.copy(name = value) }

    fun updateMinPlayers(value: String) =
        _state.update { it.copy(minPlayers = value.filter { c -> c.isDigit() }) }

    fun updateMaxPlayers(value: String) =
        _state.update { it.copy(maxPlayers = value.filter { c -> c.isDigit() }) }

    fun toggleCooperate(value: Boolean) = _state.update { it.copy(cooperate = value) }

    fun toggleExpansion(value: Boolean) = _state.update {
        it.copy(expansion = value, baseGame = if (value) it.baseGame else "")
    }

    fun selectBaseGame(value: String) = _state.update { it.copy(baseGame = value) }

    fun updateCategory(value: String) = _state.update { it.copy(category = value) }

    fun updateYear(value: String) =
        _state.update { it.copy(year = value.filter { c -> c.isDigit() }) }

    fun updateCoverUri(uri: String?) =
        _state.update { it.copy(coverUri = uri, hasCover = !uri.isNullOrBlank()) }

    private suspend fun searchBgg(name: String) {
        _state.update { it.copy(isSearching = true) }
        when (val response = boardGameApiRepository.searchGame(name)) {
            is RequestResult.Success -> {
                val matches = response.data.boardGames.orEmpty().map { element ->
                    PopularMatch(
                        id = element.id,
                        name = element.name ?: "",
                        category = "BGG",
                        year = element.yearPublished?.toString() ?: "—",
                    )
                }
                _state.update { it.copy(popularMatches = matches, isSearching = false) }
            }

            is RequestResult.Error -> {
                _state.update { it.copy(popularMatches = emptyList(), isSearching = false) }
                _events.send(AddGameEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
            }
        }
    }

    fun addFromBgg(match: PopularMatch) {
        viewModelScope.launch {
            val details = (boardGameApiRepository.getGame(match.id) as? RequestResult.Success)
                ?.data?.boardGamesBGG?.firstOrNull()
            val game = Game(
                name = match.name,
                expansion = false,
                cooperate = details?.isCooperative() == true,
                baseGame = "",
                minPlayer = details?.minPlayers?.toString() ?: "",
                maxPlayer = details?.maxPlayers?.toString() ?: "",
                uriFromBgg = details?.image ?: details?.thumbnail,
                games = 0,
                category = details?.categories?.firstOrNull()?.value,
                yearPublished = details?.yearPublished,
                playTime = details?.playingTime ?: details?.maxPlayTime,
                rating = details?.averageRating(),
                bggId = match.id,
                id = UUID.randomUUID().toString(),
            )
            save(game)
        }
    }

    fun saveManual() {
        val current = state.value
        val baseGameId = baseGames.firstOrNull { it.name == current.baseGame }?.id
        val existing = editingGame
        val game = Game(
            name = current.name,
            expansion = current.expansion && current.baseGame.isNotBlank(),
            cooperate = current.cooperate,
            baseGame = current.baseGame,
            baseGameId = baseGameId,
            minPlayer = current.minPlayers,
            maxPlayer = current.maxPlayers,
            uri = current.coverUri,
            // keep BGG-sourced fields and play count when editing
            uriFromBgg = existing?.uriFromBgg,
            games = existing?.games ?: 0,
            category = current.category.ifBlank { null },
            yearPublished = current.year.toIntOrNull(),
            playTime = existing?.playTime,
            rating = existing?.rating,
            bggId = existing?.bggId,
            id = existing?.id ?: UUID.randomUUID().toString(),
        )
        viewModelScope.launch { save(game, isEdit = current.isEditMode) }
    }

    private suspend fun save(game: Game, isEdit: Boolean = false) {
        val result = if (isEdit) gameDbRepository.editGame(game) else gameDbRepository.insertGame(game)
        when (result) {
            is RequestResult.Success ->
                _events.send(AddGameEvent.Saved(R.string.success_global, AppSnackBarType.SUCCESS))

            is RequestResult.Error ->
                _events.send(AddGameEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR))
        }
    }
}
