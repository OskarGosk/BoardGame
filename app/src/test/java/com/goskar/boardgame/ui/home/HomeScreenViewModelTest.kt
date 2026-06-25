package com.goskar.boardgame.ui.home

import com.google.firebase.auth.FirebaseAuth
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.HistoryGameFirebase
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.goskar.boardgame.data.models.HistoryGame
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private lateinit var api: BoardGameFirebaseDataRepository
    private lateinit var addAllGameToDb: UpsertAllGameUseCase
    private lateinit var addAllPlayerToDb: UpsertAllPlayerUseCase
    private lateinit var addAllHistoryToDb: UpsertAllHistoryGameUseCase
    private lateinit var addAllExpansionToDb: UpsertAllHistoryGameExpansionUseCase
    private lateinit var userSession: UserRepository
    private lateinit var clearDbUseCase: ClearDbUseCase
    private lateinit var auth: FirebaseAuth
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: HomeScreenViewModel

    private val game = Game(
        name = "Chess", expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, id = "game-1"
    )
    private val player = Player(
        name = "Alice", games = 2, winRatio = 1, description = "", selected = false, id = "player-1"
    )
    private val historyFirebase = HistoryGameFirebase(
        gameName = "Chess", winner = "Alice", gameData = "2024-03-15",
        listOfPlayer = listOf("Alice"), description = "", id = "history-1"
    )
    private val expansion = HistoryGameExpansion(
        id = "exp-1", historyGameId = "history-1", expansionName = "Exp", expansionId = "e-1"
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        mockkStatic(FirebaseAuth::class)
        auth = mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns auth
        every { auth.currentUser } returns null

        api = mockk()
        addAllGameToDb = mockk()
        addAllPlayerToDb = mockk()
        addAllHistoryToDb = mockk()
        addAllExpansionToDb = mockk()
        userSession = mockk(relaxed = true)
        clearDbUseCase = mockk()

        // Happy-path defaults; individual tests override one step to fail.
        coEvery { api.getAllGame() } returns RequestResult.Success(listOf(game))
        coEvery { api.getAllPlayer() } returns RequestResult.Success(listOf(player))
        coEvery { api.getAllHistoryGame() } returns RequestResult.Success(listOf(historyFirebase))
        coEvery { api.getAllHistoryGameExpansion() } returns RequestResult.Success(listOf(expansion))
        coEvery { addAllGameToDb.invoke(any()) } returns true
        coEvery { addAllPlayerToDb.invoke(any()) } returns true
        coEvery { addAllHistoryToDb.invoke(any()) } returns true
        coEvery { addAllExpansionToDb.invoke(any()) } returns true
        coEvery { clearDbUseCase.invoke() } returns true

        viewModel = HomeScreenViewModel(
            api, addAllGameToDb, addAllPlayerToDb, addAllHistoryToDb,
            addAllExpansionToDb, userSession, clearDbUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // -------------------------------------------------------------------------
    // getAllData() — success
    // -------------------------------------------------------------------------

    @Test
    fun getAllData_allStepsSucceed_setsIsSuccessDownloadDataTrue() = runTest(testDispatcher) {
        viewModel.getAllData()

        val state = viewModel.state.value
        assertEquals(true, state.isSuccessDownloadData)
        assertEquals(false, state.isLoading)
        coVerify(exactly = 1) { addAllGameToDb.invoke(any()) }
        coVerify(exactly = 1) { addAllExpansionToDb.invoke(any()) }
    }

    @Test
    fun getAllData_historyDownloaded_isConvertedToDtoBeforeUpsert() = runTest(testDispatcher) {
        val saved = slot<List<HistoryGame>>()
        coEvery { addAllHistoryToDb.invoke(capture(saved)) } returns true

        viewModel.getAllData()

        // Firebase string date is parsed into a LocalDate DTO before persisting.
        assertEquals(LocalDate.of(2024, 3, 15), saved.captured[0].gameData)
        assertEquals("history-1", saved.captured[0].id)
    }

    // -------------------------------------------------------------------------
    // getAllData() — short-circuit
    // -------------------------------------------------------------------------

    @Test
    fun getAllData_gameApiFails_isSuccessFalseAndSkipsRest() = runTest(testDispatcher) {
        coEvery { api.getAllGame() } returns RequestResult.Error(Throwable("network"))

        viewModel.getAllData()

        assertEquals(false, viewModel.state.value.isSuccessDownloadData)
        coVerify(exactly = 0) { addAllGameToDb.invoke(any()) }
        coVerify(exactly = 0) { api.getAllPlayer() }
    }

    @Test
    fun getAllData_gameUpsertReturnsFalse_isSuccessFalseAndSkipsRest() = runTest(testDispatcher) {
        coEvery { addAllGameToDb.invoke(any()) } returns false

        viewModel.getAllData()

        assertEquals(false, viewModel.state.value.isSuccessDownloadData)
        coVerify(exactly = 0) { api.getAllPlayer() }
    }

    // -------------------------------------------------------------------------
    // signOut()
    // -------------------------------------------------------------------------

    @Test
    fun signOut_clearsSessionAndDb_setsIsSignOut() = runTest(testDispatcher) {
        viewModel.signOut()

        assertEquals(true, viewModel.state.value.isSignOut)
        verify(exactly = 1) { auth.signOut() }
        coVerify(exactly = 1) { userSession.logout() }
        coVerify(exactly = 1) { clearDbUseCase.invoke() }
    }
}
