package com.goskar.boardgame.ui.screens.home.viewmodel

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
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
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeNewViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var historyRepository: GamesHistoryDbRepository
    private lateinit var userSession: UserRepository
    private lateinit var api: BoardGameFirebaseDataRepository
    private lateinit var addAllGameToDb: UpsertAllGameUseCase
    private lateinit var addAllPlayerToDb: UpsertAllPlayerUseCase
    private lateinit var addAllHistoryToDb: UpsertAllHistoryGameUseCase
    private lateinit var addAllHistoryGameExpansionToDb: UpsertAllHistoryGameExpansionUseCase

    private fun game(name: String, plays: Int, id: String) = Game(
        name = name, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = plays, id = id,
    )

    private fun history(gameName: String, winner: String, date: LocalDate, players: List<String>) =
        HistoryGame(gameName = gameName, winner = winner, gameData = date, listOfPlayer = players, description = "")

    private fun buildViewModel() = HomeNewViewModel(
        getAllGameUseCase, historyRepository, userSession, api,
        addAllGameToDb, addAllPlayerToDb, addAllHistoryToDb, addAllHistoryGameExpansionToDb,
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        getAllGameUseCase = mockk()
        historyRepository = mockk()
        userSession = mockk()
        api = mockk(relaxed = true)
        addAllGameToDb = mockk(relaxed = true)
        addAllPlayerToDb = mockk(relaxed = true)
        addAllHistoryToDb = mockk(relaxed = true)
        addAllHistoryGameExpansionToDb = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun load_computesTotalMostPlayedAndUserName() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns listOf(
            game("Wingspan", plays = 5, id = "g1"),
            game("Root", plays = 2, id = "g2"),
        )
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(
            listOf(
                history("Wingspan", "Alex", LocalDate.of(2024, 1, 2), listOf("Alex", "Bob")),
                history("Root", "Bob", LocalDate.of(2024, 1, 1), listOf("Alex", "Bob")),
            )
        )
        coEvery { userSession.getCurrentSession() } returns User(email = "alex@example.com", token = null, userUID = null)

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        val state = viewModel.state.value
        assertEquals("Alex", state.userName)
        assertEquals("2", state.totalGames)
        assertEquals("Wingspan", state.mostPlayed)
        assertEquals("50%", state.winRatio)
        assertEquals(2, state.recentSessions.size)
        assertEquals("Wingspan", state.recentSessions.first().gameName)
    }

    @Test
    fun load_guestSession_userNameIsGuest() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(emptyList())
        coEvery { userSession.getCurrentSession() } returns User(email = null, token = null, userUID = "guest")

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        assertEquals("Guest", viewModel.state.value.userName)
        assertEquals("0", viewModel.state.value.totalGames)
        assertEquals("—", viewModel.state.value.mostPlayed)
    }


    @Test
    fun load_firstLogin_syncsFromCloud() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(emptyList())
        coEvery { userSession.getCurrentSession() } returns null
        coEvery { api.getAllGame() } returns RequestResult.Error(Throwable("offline"))

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = true)

        coVerify(exactly = 1) { api.getAllGame() }
    }

    @Test
    fun load_notFirstLogin_doesNotSyncFromCloud() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(emptyList())
        coEvery { userSession.getCurrentSession() } returns null

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        coVerify(exactly = 0) { api.getAllGame() }
    }
}
