package com.goskar.boardgame.ui.screens.logGameplay.viewmodel

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.CoopWinner
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.GameMode
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddGameplayViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var playerDbRepository: PlayerDbRepository
    private lateinit var gameDbRepository: GameDbRepository
    private lateinit var gamesHistoryDbRepository: GamesHistoryDbRepository
    private lateinit var getAllGameUseCase: GetAllGameUseCase

    private lateinit var savedHistory: io.mockk.CapturingSlot<HistoryGame>
    private lateinit var editedPlayers: MutableList<Player>
    private lateinit var editedGames: MutableList<Game>

    private fun game(
        name: String,
        id: String,
        max: String,
        coop: Boolean,
        games: Int = 0,
        modes: List<GameMode>? = null,
    ) = Game(
        name = name, expansion = false, cooperate = coop, baseGame = "",
        minPlayer = "1", maxPlayer = max, games = games, id = id, modes = modes,
    )

    private fun player(name: String, id: String, games: Int, winRatio: Int) =
        Player(name = name, games = games, winRatio = winRatio, description = "", selected = false, id = id)

    private fun buildViewModel() = AddGameplayViewModel(
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
        coEvery { gamesHistoryDbRepository.getAllHistoryGame() } returns RequestResult.Success(emptyList())
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

    private val fourPlayers = listOf(
        player("Alex", "p1", 0, 0), player("Bob", "p2", 0, 0),
        player("Cara", "p3", 0, 0), player("Dan", "p4", 0, 0),
    )

    private fun session(gameName: String, day: Int, mode: GameMode?, winner: String = "Alex") = HistoryGame(
        gameName = gameName, winner = winner, gameData = LocalDate.of(2026, 1, day),
        listOfPlayer = listOf("Alex"), description = "", mode = mode,
    )

    @Test
    fun selectGame_singleModeGame_offersOnlyThatMode() = runTest(testDispatcher) {
        stubData(game("Catan", "g1", max = "4", coop = false), fourPlayers)
        val viewModel = buildViewModel()

        viewModel.load(preselectedGameId = "g1")

        assertEquals(listOf(GameMode.COMPETITIVE), viewModel.state.value.availableModes)
        assertEquals(GameMode.COMPETITIVE, viewModel.state.value.mode)
    }

    @Test
    fun selectMode_notOfferedByTheGame_isIgnored() = runTest(testDispatcher) {
        stubData(game("Pandemic", "g1", max = "4", coop = true), fourPlayers)
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")

        viewModel.selectMode(GameMode.TEAM)

        assertEquals(GameMode.COOPERATIVE, viewModel.state.value.mode)
    }

    @Test
    fun selectGame_multiModeGame_startsInTheModeOfItsLatestSession() = runTest(testDispatcher) {
        stubData(
            game("Spirit Island", "g1", max = "4", coop = true, modes = listOf(GameMode.COMPETITIVE, GameMode.COOPERATIVE)),
            fourPlayers,
        )
        coEvery { gamesHistoryDbRepository.getAllHistoryGame() } returns RequestResult.Success(
            listOf(
                session("Spirit Island", day = 1, mode = GameMode.COMPETITIVE),
                session("Spirit Island", day = 9, mode = GameMode.COOPERATIVE),
                session("Other", day = 20, mode = GameMode.TEAM),
            )
        )
        val viewModel = buildViewModel()

        viewModel.load(preselectedGameId = "g1")

        assertEquals(GameMode.COOPERATIVE, viewModel.state.value.mode)
        assertEquals(listOf(GameMode.COMPETITIVE, GameMode.COOPERATIVE), viewModel.state.value.availableModes)
    }

    @Test
    fun selectMode_clearsTheResultPickedForTheOtherMode() = runTest(testDispatcher) {
        stubData(
            game("Spirit Island", "g1", max = "4", coop = false, modes = listOf(GameMode.COMPETITIVE, GameMode.COOPERATIVE)),
            fourPlayers,
        )
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.selectWinner(0)

        viewModel.selectMode(GameMode.COOPERATIVE)

        assertEquals(-1, viewModel.state.value.winnerIndex)
        assertEquals(null, viewModel.state.value.coopPlayersWon)
    }

    @Test
    fun teamMode_newPlayersJoinTheSmallestTeam() = runTest(testDispatcher) {
        stubData(game("Codenames", "g1", max = "8", coop = false, modes = listOf(GameMode.TEAM)), fourPlayers)
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")

        (0..3).forEach { viewModel.togglePlayer(it) }

        assertEquals(listOf(1, 2, 1, 2), viewModel.state.value.players.map { it.team })
    }

    @Test
    fun teamMode_deselectingTheLastMemberOfTheWinningTeam_clearsIt() = runTest(testDispatcher) {
        stubData(game("Codenames", "g1", max = "8", coop = false, modes = listOf(GameMode.TEAM)), fourPlayers)
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0) // team 1
        viewModel.togglePlayer(1) // team 2
        viewModel.selectWinningTeam(2)

        viewModel.togglePlayer(1)

        assertEquals(null, viewModel.state.value.winningTeam)
    }

    @Test
    fun teamMode_addTeam_isCappedAtTheNumberOfSelectedPlayers() = runTest(testDispatcher) {
        stubData(game("Codenames", "g1", max = "8", coop = false, modes = listOf(GameMode.TEAM)), fourPlayers)
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        (0..2).forEach { viewModel.togglePlayer(it) }

        repeat(5) { viewModel.addTeam() }

        assertEquals(3, viewModel.state.value.teamCount)
    }

    @Test
    fun logSession_teamMode_creditsEveryMemberOfTheWinningTeam() = runTest(testDispatcher) {
        stubData(game("Codenames", "g1", max = "8", coop = false, modes = listOf(GameMode.TEAM)), fourPlayers)
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        (0..3).forEach { viewModel.togglePlayer(it) } // Alex 1, Bob 2, Cara 1, Dan 2
        viewModel.selectWinningTeam(1)

        viewModel.logSession()

        val saved = savedHistory.captured
        assertEquals(GameMode.TEAM, saved.mode)
        assertEquals(1, saved.winningTeam)
        assertEquals(mapOf("Alex" to 1, "Bob" to 2, "Cara" to 1, "Dan" to 2), saved.teams)
        assertEquals("Alex, Cara", saved.winner)
        assertEquals(1, editedPlayers.first { it.id == "p1" }.winRatio)
        assertEquals(1, editedPlayers.first { it.id == "p3" }.winRatio)
        assertEquals(0, editedPlayers.first { it.id == "p2" }.winRatio)
        assertEquals(0, editedPlayers.first { it.id == "p4" }.winRatio)
    }

    @Test
    fun logSession_teamMode_everyoneInOneTeam_emitsPickTeams() = runTest(testDispatcher) {
        stubData(game("Codenames", "g1", max = "8", coop = false, modes = listOf(GameMode.TEAM)), fourPlayers)
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.togglePlayer(1)
        viewModel.assignTeam(1, 1)
        viewModel.selectWinningTeam(1)

        viewModel.events.test {
            viewModel.logSession()
            assertEquals(
                AddGameplayEvent.ShowMessage(R.string.log_pick_teams, AppSnackBarType.ERROR),
                awaitItem(),
            )
        }
    }

    @Test
    fun logSession_nonTeamMode_savesNoTeams() = runTest(testDispatcher) {
        stubData(game("Catan", "g1", max = "4", coop = false), fourPlayers)
        val viewModel = buildViewModel()
        viewModel.load(preselectedGameId = "g1")
        viewModel.togglePlayer(0)
        viewModel.selectWinner(0)

        viewModel.logSession()

        assertEquals(GameMode.COMPETITIVE, savedHistory.captured.mode)
        assertEquals(null, savedHistory.captured.teams)
        assertEquals(null, savedHistory.captured.winningTeam)
    }
}
