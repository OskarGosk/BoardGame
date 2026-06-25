package com.goskar.boardgame.ui.gamesHistory

import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetHistoryWithExpansionUseCase
import com.goskar.boardgame.data.useCase.HistoryGameWithExpansion
import io.mockk.coEvery
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
class GamesHistoryViewModelTest {

    private lateinit var repo: GamesHistoryDbRepository
    private lateinit var useCase: GetHistoryWithExpansionUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: GamesHistoryViewModel

    private fun historyGame(name: String, date: LocalDate, id: String = name) = HistoryGame(
        gameName = name,
        winner = "Winner",
        gameData = date,
        listOfPlayer = listOf("P1"),
        description = "",
        id = id
    )

    private fun withExpansion(name: String, date: LocalDate) = HistoryGameWithExpansion(
        history = historyGame(name, date),
        expansion = emptyList<HistoryGameExpansion>()
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        useCase = mockk()

        // Defaults so the init{} block (which calls both loaders) succeeds quietly.
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(emptyList())
        coEvery { useCase.invoke() } returns RequestResult.Success(emptyList())

        viewModel = GamesHistoryViewModel(repo, useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // getAllHistoryGame()
    // -------------------------------------------------------------------------

    @Test
    fun getAllHistoryGame_success_sortsByDateAscendingAndClearsLoading() = runTest(testDispatcher) {
        val jan = historyGame("Jan game", LocalDate.of(2024, 1, 1))
        val mar = historyGame("Mar game", LocalDate.of(2024, 3, 1))
        val feb = historyGame("Feb game", LocalDate.of(2024, 2, 1))
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(listOf(mar, jan, feb))

        viewModel.getAllHistoryGame()

        val state = viewModel.state.value
        assertEquals(listOf(jan, feb, mar), state.historyList)
        assertEquals(false, state.loading)
        assertEquals(false, state.errorVisible)
    }

    @Test
    fun getAllHistoryGame_error_setsErrorVisibleAndClearsLoading() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Error(Throwable("db error"))

        viewModel.getAllHistoryGame()

        val state = viewModel.state.value
        assertEquals(true, state.errorVisible)
        assertEquals(false, state.loading)
    }

    // -------------------------------------------------------------------------
    // init -> validateGetHistoryGameWithExpansion()
    // -------------------------------------------------------------------------

    @Test
    fun init_historyWithExpansionSuccess_sortsByDateAscending() = runTest(testDispatcher) {
        val a = withExpansion("A", LocalDate.of(2024, 5, 1))
        val b = withExpansion("B", LocalDate.of(2024, 2, 1))
        val c = withExpansion("C", LocalDate.of(2024, 8, 1))
        coEvery { useCase.invoke() } returns RequestResult.Success(listOf(a, c, b))

        viewModel = GamesHistoryViewModel(repo, useCase)

        assertEquals(listOf(b, a, c), viewModel.state.value.historyGameWithExpansion)
    }

    @Test
    fun init_historyWithExpansionError_setsErrorVisible() = runTest(testDispatcher) {
        coEvery { useCase.invoke() } returns RequestResult.Error(Throwable("expansion error"))

        viewModel = GamesHistoryViewModel(repo, useCase)

        assertEquals(true, viewModel.state.value.errorVisible)
    }
}
