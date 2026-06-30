package com.goskar.boardgame.ui.gameReports

import app.cash.turbine.test
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.gameReports.components.RowChartVariantsEnum
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GameReportsViewModelTest {

    private lateinit var historyDbRepository: GamesHistoryDbRepository
    private lateinit var allGameUseCase: GetAllGameUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: GameReportsViewModel

    private fun historyGame(year: Int, month: Int, day: Int) = HistoryGame(
        gameName = "Game",
        winner = "Winner",
        gameData = LocalDate.of(year, month, day),
        listOfPlayer = listOf("P1"),
        description = ""
    )

    private fun game(name: String, games: Int) = Game(
        name = name,
        expansion = false,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = games,
        id = name
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        historyDbRepository = mockk()
        allGameUseCase = mockk()

        coEvery { allGameUseCase.invoke() } returns emptyList()
        coEvery { historyDbRepository.getAllHistoryGame() } returns RequestResult.Success(emptyList())

        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // YEAR mode
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_yearMode_twoYearsWithGames_returnsBarPerYear() = runTest(testDispatcher) {
        val history = listOf(historyGame(2023, 1, 1), historyGame(2024, 1, 1))
        coEvery { historyDbRepository.getAllHistoryGame() } returns RequestResult.Success(history)
        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)
        
        viewModel.state.test {
            expectMostRecentItem()
            viewModel.prepareChart()
            val data = viewModel.chartData.value
            assertEquals(2, data.size)
        }
    }

    @Test
    fun prepareChart_yearMode_yearWithNoGames_hasNoBar() = runTest(testDispatcher) {
        val history = listOf(historyGame(2022, 1, 1), historyGame(2024, 1, 1))
        coEvery { historyDbRepository.getAllHistoryGame() } returns RequestResult.Success(history)
        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)
        
        viewModel.state.test {
            expectMostRecentItem()
            viewModel.prepareChart()
            val data = viewModel.chartData.value
            assertTrue(data.none { it.label == "2023" })
        }
    }

    @Test
    fun prepareChart_yearMode_emptyHistory_chartDataIsEmpty() = runTest(testDispatcher) {
        viewModel.state.test {
            expectMostRecentItem()
            viewModel.prepareChart()
            assertTrue(viewModel.chartData.value.isEmpty())
        }
    }

    // -------------------------------------------------------------------------
    // MONTHLY mode
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_monthlyMode_alwaysReturns12Bars() = runTest(testDispatcher) {
        viewModel.state.test {
            expectMostRecentItem()
            viewModel.selectYear(2024)
            expectMostRecentItem()
            viewModel.prepareChart()
            assertEquals(12, viewModel.chartData.value.size)
        }
    }

    @Test
    fun prepareChart_monthlyMode_countIsCorrectPerMonth() = runTest(testDispatcher) {
        val history = listOf(historyGame(2024, 1, 1), historyGame(2024, 1, 2), historyGame(2024, 3, 1))
        coEvery { historyDbRepository.getAllHistoryGame() } returns RequestResult.Success(history)
        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)

        viewModel.state.test {
            expectMostRecentItem()
            viewModel.selectYear(2024)
            expectMostRecentItem()
            viewModel.prepareChart()
            val data = viewModel.chartData.value
            assertEquals(2.0, data.find { it.label == "Jan" }?.values?.get(0)?.value ?: 0.0, 0.0)
            assertEquals(1.0, data.find { it.label == "Mar" }?.values?.get(0)?.value ?: 0.0, 0.0)
        }
    }

    // -------------------------------------------------------------------------
    // MONTH mode
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_monthMode_onlyDaysWithGamesGetBars() = runTest(testDispatcher) {
        val history = listOf(historyGame(2024, 6, 5), historyGame(2024, 6, 20))
        coEvery { historyDbRepository.getAllHistoryGame() } returns RequestResult.Success(history)
        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)

        viewModel.state.test {
            expectMostRecentItem()
            viewModel.selectYear(2024)
            viewModel.useMonthChart(6)
            expectMostRecentItem()
            viewModel.prepareChart()
            val data = viewModel.chartData.value
            assertEquals(2, data.size)
        }
    }

    // -------------------------------------------------------------------------
    // PERIOD mode
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_periodMode_gameOnDateInRange_getsBar() = runTest(testDispatcher) {
        val history = listOf(historyGame(2024, 6, 10))
        coEvery { historyDbRepository.getAllHistoryGame() } returns RequestResult.Success(history)
        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)

        viewModel.state.test {
            expectMostRecentItem()
            viewModel.useRowChartVariant(RowChartVariantsEnum.PERIOD)
            viewModel.updateStartEndDate(
                startDate = LocalDate.of(2024, 6, 8),
                endDate = LocalDate.of(2024, 6, 12)
            )
            expectMostRecentItem()
            viewModel.prepareChart()
            assertEquals(1, viewModel.chartData.value.size)
        }
    }

    @Test
    fun prepareChart_periodMode_gameOutsideRange_notIncluded() = runTest(testDispatcher) {
        val history = listOf(historyGame(2024, 6, 1))
        coEvery { historyDbRepository.getAllHistoryGame() } returns RequestResult.Success(history)
        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)

        viewModel.state.test {
            expectMostRecentItem()
            viewModel.useRowChartVariant(RowChartVariantsEnum.PERIOD)
            viewModel.updateStartEndDate(
                startDate = LocalDate.of(2024, 6, 5),
                endDate = LocalDate.of(2024, 6, 15),
            )
            expectMostRecentItem()
            viewModel.prepareChart()
            assertTrue(viewModel.chartData.value.isEmpty())
        }
    }

    // -------------------------------------------------------------------------
    // All Time Data
    // -------------------------------------------------------------------------

    @Test
    fun init_playGamesAllTimeData_correctProportionPerGame() = runTest(testDispatcher) {
        val games = listOf(game("G1", 3), game("G2", 1))
        coEvery { allGameUseCase.invoke() } returns games
        viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)

        viewModel.rowChartData.test {
            val data = awaitItem()
            assertEquals(0.75, data[0].values[0].value, 0.0)
            assertEquals(0.25, data[1].values[0].value, 0.0)
        }
    }
}
