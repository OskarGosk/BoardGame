package com.goskar.boardgame.ui.screens.profile.viewmodel

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.UploadToCloudUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileNewViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var userSession: UserRepository
    private lateinit var playerDbRepository: PlayerDbRepository
    private lateinit var historyRepository: GamesHistoryDbRepository
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var mePlayerRepository: MePlayerRepository
    private lateinit var clearDbUseCase: ClearDbUseCase
    private lateinit var uploadToCloud: UploadToCloudUseCase

    private fun player(name: String, games: Int, winRatio: Int, id: String) =
        Player(name = name, games = games, winRatio = winRatio, description = "", selected = false, id = id)

    private fun game(id: String) = Game(
        name = id, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, id = id,
    )

    private fun history(id: String) =
        HistoryGame(gameName = "g", winner = "w", gameData = LocalDate.now(), listOfPlayer = emptyList(), description = "", id = id)

    private fun buildViewModel() = ProfileNewViewModel(
        userSession, playerDbRepository, historyRepository, getAllGameUseCase, mePlayerRepository, clearDbUseCase, uploadToCloud,
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        userSession = mockk()
        playerDbRepository = mockk()
        historyRepository = mockk()
        getAllGameUseCase = mockk()
        mePlayerRepository = mockk(relaxed = true)
        clearDbUseCase = mockk(relaxed = true)
        uploadToCloud = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_linkedPlayer_showsRealStats() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "alex@example.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(
            listOf(player("Alex M.", games = 4, winRatio = 3, id = "p1"))
        )
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns "p1"

        val viewModel = buildViewModel()
        viewModel.load()

        val state = viewModel.state.value
        assertFalse(state.needsPlayerSelection)
        assertFalse(state.isGuest)
        assertEquals("Alex M.", state.name)
        assertEquals("alex@example.com", state.subtitle)
        assertEquals("4", state.gamesLogged)
        assertEquals("75%", state.winRate)
    }

    @Test
    fun load_notLinked_showsPicker() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "alex@example.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(
            listOf(
                player("Alex M.", games = 4, winRatio = 3, id = "p1"),
                player("Bob", games = 2, winRatio = 1, id = "p2"),
            )
        )
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null

        val viewModel = buildViewModel()
        viewModel.load()

        val state = viewModel.state.value
        assertTrue(state.needsPlayerSelection)
        assertEquals(2, state.availablePlayers.size)
        assertEquals("alex", state.name)
        assertEquals("—", state.winRate)
    }

    @Test
    fun load_guest_showsGlobalAggregates() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = null, token = "t", userUID = "guest")
        coEvery { getAllGameUseCase.invoke() } returns listOf(game("g1"), game("g2"), game("g3"))
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(
            listOf(history("h1"), history("h2"))
        )

        val viewModel = buildViewModel()
        viewModel.load()

        val state = viewModel.state.value
        assertTrue(state.isGuest)
        assertFalse(state.needsPlayerSelection)
        assertEquals("Guest", state.name)
        assertEquals("2", state.gamesLogged)
        assertEquals("—", state.winRate)
    }

    @Test
    fun forceSync_success_emitsSuccessAndUpdatesState() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "a@b.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(emptyList())
        coEvery { uploadToCloud() } returns true

        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.forceSync()
            assertEquals(
                ProfileEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS),
                awaitItem(),
            )
        }
        assertEquals(false, viewModel.state.value.syncing)
        assertEquals("Synced just now", viewModel.state.value.lastSynced)
    }

    @Test
    fun forceSync_failure_emitsError() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "a@b.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(emptyList())
        coEvery { uploadToCloud() } returns false

        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.forceSync()
            assertEquals(
                ProfileEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem(),
            )
        }
    }

    @Test
    fun selectPlayer_linksAndReloadsAsLinked() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "alex@example.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(
            listOf(player("Alex M.", games = 4, winRatio = 3, id = "p1"))
        )
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns "p1"

        val viewModel = buildViewModel()
        viewModel.selectPlayer("p1")

        coVerify(exactly = 1) { mePlayerRepository.linkPlayer("uid1", "p1") }
        val state = viewModel.state.value
        assertFalse(state.needsPlayerSelection)
        assertEquals("Alex M.", state.name)
    }
}
