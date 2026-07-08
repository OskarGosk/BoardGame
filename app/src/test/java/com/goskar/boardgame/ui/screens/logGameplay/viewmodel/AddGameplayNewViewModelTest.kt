package com.goskar.boardgame.ui.screens.logGameplay.viewmodel

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddGameplayNewViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var playerDbRepository: PlayerDbRepository
    private lateinit var gameDbRepository: GameDbRepository
    private lateinit var gamesHistoryDbRepository: GamesHistoryDbRepository
    private lateinit var getAllGameUseCase: GetAllGameUseCase

    private lateinit var savedHistory: io.mockk.CapturingSlot<HistoryGame>
    private lateinit var editedPlayers: MutableList<Player>
    private lateinit var editedGames: MutableList<Game>

    private fun game(name: String, id: String, max: String, coop: Boolean, games: Int = 0) = Game(
        name = name, expansion = false, cooperate = coop, baseGame = "",
        minPlayer = "1", maxPlayer = max, games = games, id = id,
    )

    private fun player(name: String, id: String, games: Int, winRatio: Int) =
        Player(name = name, games = games, winRatio = winRatio, description = "", selected = false, id = id)

    private fun buildViewModel() = AddGameplayNewViewModel(
        playerDbRepository, gameDbRepository, gamesHistoryDbRepository, getAllGameUseCase,
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        playerDbRepository = mockk()
        gameDbRepository = mockk()
        gamesHistoryDbRepository = mockk()
        getAllGameUseCase = mockk()

        savedHistory = slot()
        editedPlayers = mutableListOf()
        editedGames = mutableListOf()
        coEvery { gamesHistoryDbRepository.insertHistoryGame(capture(savedHistory)) } returns RequestResult.Success(true)
        coEvery { gameDbRepository.editGame(capture(editedGames)) } returns RequestResult.Success(true)
        coEvery { playerDbRepository.editPlayer(capture(editedPlayers)) } returns RequestResult.Success(true)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun stubData(game: Game, players: List<Player>) {
        coEvery { getAllGameUseCase.invoke() } returns listOf(game)
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(players)
    }

    @Test
    fun load_preselectedGame_selectsItWithCoopAndMax() = runTest(testDispatcher) {
        stubData(game("Pandemic", "g1", max = "4", coop = true), listOf(player("Alex", "p1", 0, 0)))

        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")

        val state = viewModel.state.value
        assertEquals("g1", state.selectedGameId)
        assertEquals("Pandemic", state.selectedGame)
        assertEquals(true, state.cooperate)
        assertEquals(4, state.maxPlayers)
    }

    @Test
    fun togglePlayer_blocksMoreThanMaxPlayers() = runTest(testDispatcher) {
        stubData(
            game("Duel", "g1", max = "2", coop = false),
            listOf(player("A", "p1", 0, 0), player("B", "p2", 0, 0), player("C", "p3", 0, 0)),
        )
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")

        viewModel.togglePlayer(0)
        viewModel.togglePlayer(1)
        viewModel.togglePlayer(2) // exceeds max (2) -> blocked

        val selected = viewModel.state.value.players.filter { it.selected }
        assertEquals(2, selected.size)
        assertEquals(false, viewModel.state.value.players[2].selected)
    }

    @Test
    fun togglePlayer_deselectingWinner_clearsWinner() = runTest(testDispatcher) {
        stubData(
            game("Catan", "g1", max = "4", coop = false),
            listOf(player("Alex", "p1", 0, 0), player("Bob", "p2", 0, 0)),
        )
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.togglePlayer(1)
        viewModel.selectWinner(0)

        viewModel.togglePlayer(0) // deselect the winner

        assertEquals(-1, viewModel.state.value.winnerIndex)
    }

    @Test
    fun logSession_noWinner_emitsPickWinner() = runTest(testDispatcher) {
        stubData(
            game("Catan", "g1", max = "4", coop = false),
            listOf(player("Alex", "p1", 0, 0), player("Bob", "p2", 0, 0)),
        )
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.togglePlayer(1)

        viewModel.events.test {
            viewModel.logSession()
            assertEquals(
                AddGameplayEvent.ShowMessage(R.string.log_pick_winner, AppSnackBarType.ERROR),
                awaitItem(),
            )
        }
    }

    @Test
    fun logSession_nonCoop_incrementsGameAndWinnerOnly() = runTest(testDispatcher) {
        stubData(
            game("Catan", "g1", max = "4", coop = false, games = 5),
            listOf(player("Alex", "p1", games = 2, winRatio = 1), player("Bob", "p2", games = 3, winRatio = 0)),
        )
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.togglePlayer(1)
        viewModel.selectWinner(0) // Alex wins

        viewModel.logSession()

        assertEquals("Alex", savedHistory.captured.winner)
        assertEquals(listOf("Alex", "Bob"), savedHistory.captured.listOfPlayer)
        assertEquals(6, editedGames.single().games) // 5 + 1
        assertEquals(2, editedPlayers.first { it.id == "p1" }.winRatio) // Alex 1 + 1
        assertEquals(3, editedPlayers.first { it.id == "p1" }.games)    // 2 + 1
        assertEquals(0, editedPlayers.first { it.id == "p2" }.winRatio) // Bob unchanged
        assertEquals(4, editedPlayers.first { it.id == "p2" }.games)    // 3 + 1
    }

    @Test
    fun logSession_coopPlayersWon_incrementsEveryone() = runTest(testDispatcher) {
        stubData(
            game("Pandemic", "g1", max = "4", coop = true),
            listOf(player("Alex", "p1", 0, 0), player("Bob", "p2", 0, 0)),
        )
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.togglePlayer(1)
        viewModel.selectCoopWin(true)

        viewModel.logSession()

        assertEquals(CoopWinner.PLAYERS, savedHistory.captured.winner)
        assertEquals(1, editedPlayers.first { it.id == "p1" }.winRatio)
        assertEquals(1, editedPlayers.first { it.id == "p2" }.winRatio)
    }

    @Test
    fun logSession_coopGameWon_incrementsNobody() = runTest(testDispatcher) {
        stubData(
            game("Pandemic", "g1", max = "4", coop = true),
            listOf(player("Alex", "p1", 0, 0), player("Bob", "p2", 0, 0)),
        )
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.togglePlayer(1)
        viewModel.selectCoopWin(false)

        viewModel.logSession()

        assertEquals(CoopWinner.COMPUTER, savedHistory.captured.winner)
        assertEquals(0, editedPlayers.first { it.id == "p1" }.winRatio)
        assertEquals(0, editedPlayers.first { it.id == "p2" }.winRatio)
        assertEquals(1, editedPlayers.first { it.id == "p1" }.games) // still counts a play
    }
}
