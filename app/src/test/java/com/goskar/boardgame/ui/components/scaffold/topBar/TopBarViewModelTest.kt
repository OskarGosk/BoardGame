package com.goskar.boardgame.ui.components.scaffold.topBar

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.HistoryGameFirebase
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.GetAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.GetAllPlayerUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
class TopBarViewModelTest {

    private lateinit var api: BoardGameFirebaseDataRepository
    private lateinit var getAllGameDb: GetAllGameUseCase
    private lateinit var getAllPlayerDb: GetAllPlayerUseCase
    private lateinit var getAllHistoryDb: GetAllHistoryGameUseCase
    private lateinit var getAllExpansionDb: GetAllHistoryGameExpansionUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: TopBarViewModel

    private val game = Game(
        name = "Chess", expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, id = "game-1"
    )
    private val player = Player(
        name = "Alice", games = 2, winRatio = 1, description = "", selected = false, id = "player-1"
    )
    private val history = HistoryGame(
        gameName = "Chess", winner = "Alice", gameData = LocalDate.of(2024, 3, 15),
        listOfPlayer = listOf("Alice"), description = "", id = "history-1"
    )
    private val expansion = HistoryGameExpansion(
        id = "exp-1", historyGameId = "history-1", expansionName = "Exp", expansionId = "e-1"
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        getAllGameDb = mockk()
        getAllPlayerDb = mockk()
        getAllHistoryDb = mockk()
        getAllExpansionDb = mockk()

        // Happy path defaults; individual tests override one step to fail.
        coEvery { getAllGameDb.invoke() } returns listOf(game)
        coEvery { getAllPlayerDb.invoke() } returns listOf(player)
        coEvery { getAllHistoryDb.invoke() } returns listOf(history)
        coEvery { getAllExpansionDb.invoke() } returns listOf(expansion)
        coEvery { api.addAllGame(any()) } returns RequestResult.Success(true)
        coEvery { api.addPlayer(any()) } returns RequestResult.Success(true)
        coEvery { api.addHistoryGame(any()) } returns RequestResult.Success(true)
        coEvery { api.addHistoryGameExpansion(any()) } returns RequestResult.Success(true)

        viewModel = TopBarViewModel(api, getAllGameDb, getAllPlayerDb, getAllHistoryDb, getAllExpansionDb)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // uploadDataToFirebase() — success
    // -------------------------------------------------------------------------

    @Test
    fun uploadDataToFirebase_allStepsSucceed_setsIsSuccessTrueAndClearsLoading() = runTest(testDispatcher) {
        viewModel.uploadDataToFirebase()

        val state = viewModel.state.value
        assertEquals(true, state.isSuccess)
        assertEquals(false, state.isLoading)
        coVerify(exactly = 1) { api.addAllGame(any()) }
        coVerify(exactly = 1) { api.addPlayer(any()) }
        coVerify(exactly = 1) { api.addHistoryGame(any()) }
        coVerify(exactly = 1) { api.addHistoryGameExpansion(any()) }
    }

    @Test
    fun uploadDataToFirebase_keysFirebaseMapsById() = runTest(testDispatcher) {
        val gameMap = slot<Map<String, Game>>()
        val historyMap = slot<Map<String, HistoryGameFirebase>>()
        coEvery { api.addAllGame(capture(gameMap)) } returns RequestResult.Success(true)
        coEvery { api.addHistoryGame(capture(historyMap)) } returns RequestResult.Success(true)

        viewModel.uploadDataToFirebase()

        assertEquals(setOf("game-1"), gameMap.captured.keys)
        // History is converted to its Firebase form (LocalDate -> ISO String) before upload.
        assertEquals(setOf("history-1"), historyMap.captured.keys)
        assertEquals("2024-03-15", historyMap.captured["history-1"]?.gameData)
    }

    // -------------------------------------------------------------------------
    // uploadDataToFirebase() — short-circuit on first failing step
    // -------------------------------------------------------------------------

    @Test
    fun uploadDataToFirebase_gameStepFails_isSuccessFalseAndSkipsRest() = runTest(testDispatcher) {
        coEvery { api.addAllGame(any()) } returns RequestResult.Error(Throwable("network"))

        viewModel.uploadDataToFirebase()

        assertEquals(false, viewModel.state.value.isSuccess)
        coVerify(exactly = 0) { api.addPlayer(any()) }
        coVerify(exactly = 0) { api.addHistoryGame(any()) }
        coVerify(exactly = 0) { api.addHistoryGameExpansion(any()) }
    }

    @Test
    fun uploadDataToFirebase_playerStepFails_isSuccessFalseAndSkipsRest() = runTest(testDispatcher) {
        coEvery { api.addPlayer(any()) } returns RequestResult.Error(Throwable("network"))

        viewModel.uploadDataToFirebase()

        assertEquals(false, viewModel.state.value.isSuccess)
        coVerify(exactly = 1) { api.addAllGame(any()) }
        coVerify(exactly = 0) { api.addHistoryGame(any()) }
        coVerify(exactly = 0) { api.addHistoryGameExpansion(any()) }
    }

    @Test
    fun uploadDataToFirebase_historyStepFails_isSuccessFalseAndSkipsExpansion() = runTest(testDispatcher) {
        coEvery { api.addHistoryGame(any()) } returns RequestResult.Error(Throwable("network"))

        viewModel.uploadDataToFirebase()

        assertEquals(false, viewModel.state.value.isSuccess)
        coVerify(exactly = 0) { api.addHistoryGameExpansion(any()) }
    }

    @Test
    fun uploadDataToFirebase_expansionStepFails_isSuccessFalse() = runTest(testDispatcher) {
        coEvery { api.addHistoryGameExpansion(any()) } returns RequestResult.Error(Throwable("network"))

        viewModel.uploadDataToFirebase()

        assertEquals(false, viewModel.state.value.isSuccess)
        coVerify(exactly = 1) { api.addHistoryGame(any()) }
        coVerify(exactly = 1) { api.addHistoryGameExpansion(any()) }
    }
}
