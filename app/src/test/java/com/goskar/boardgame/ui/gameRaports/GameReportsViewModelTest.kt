// Tier 3 — chart data aggregation across four time-grouping modes
//
// Required deps to add before implementing:
//   testImplementation("io.mockk:mockk:1.13.14")
//   testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
//
// Setup pattern:
//   @Before: Dispatchers.setMain(UnconfinedTestDispatcher())
//            historyDbRepository = mockk<GamesHistoryDbRepository>()
//            allGameUseCase      = mockk<GetAllGameUseCase>()
//            // Stub defaults before construction so init{} does not crash:
//            coEvery { allGameUseCase.invoke() } returns emptyList()
//            coEvery { historyDbRepository.getAllHistoryGame() } returns
//                RequestResult.Success(emptyList())
//            viewModel = GameReportsViewModel(historyDbRepository, allGameUseCase)
//   @After:  Dispatchers.resetMain()
//
// All chart-building functions are PRIVATE and are triggered via:
//   prepareChart() → dispatches to the correct private function based on
//                    state.selectedRowChartVariant (RowChartVariantsEnum)
//
// Controlling internal data:
//   _gameHistory is set during init from historyDbRepository.getAllHistoryGame().
//   Stub this mock BEFORE constructing the ViewModel to control what history data
//   the aggregation functions operate on. If you need different data per test,
//   construct a new ViewModel per test (or re-stub and re-trigger init — harder).
//   Recommended: one ViewModel per test method, or group tests by history dataset.
//
// Chart output format:
//   chartData.value: List<Bars>
//   Each Bars has: label (String) and values (List<Bars.Data> with a Double value).
//   Assert on bars.label and bars.values[0].value.

package com.goskar.boardgame.ui.gameRaports

import com.goskar.boardgame.ui.gameRaports.components.RowChartVariantsEnum
import org.junit.After
import org.junit.Before
import org.junit.Test

class GameReportsViewModelTest {

    @Before
    fun setUp() {
        // Dispatchers.setMain(UnconfinedTestDispatcher())
        // historyDbRepository = mockk<GamesHistoryDbRepository>()
        // allGameUseCase      = mockk<GetAllGameUseCase>()
    }

    @After
    fun tearDown() {
        // Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // YEAR mode — yearsPlaysTimeData()
    // Triggered by prepareChart() when selectedRowChartVariant == YEAR
    // Groups history by gameData.year; only years with >0 games get a bar
    // state.minYear and state.maxYear control the year range iterated
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_yearMode_twoYearsWithGames_returnsBarPerYear() {
        // Given: history contains 2 games in 2023 and 1 game in 2024
        //        state.minYear=2023, state.maxYear=2024 (set via update())
        //        state.selectedRowChartVariant = RowChartVariantsEnum.YEAR
        // When:  prepareChart() is called
        // Then:  chartData has 2 bars
        //        bar with label "2023" has value 2.0
        //        bar with label "2024" has value 1.0
    }

    @Test
    fun prepareChart_yearMode_yearWithNoGames_hasNoBar() {
        // Given: history contains games in 2022 and 2024, none in 2023
        //        state.minYear=2022, state.maxYear=2024
        // When:  prepareChart() is called
        // Then:  chartData has 2 bars (2022 and 2024 only)
        //        no bar with label "2023" exists
    }

    @Test
    fun prepareChart_yearMode_emptyHistory_chartDataIsEmpty() {
        // Given: historyDbRepository.getAllHistoryGame() returns Success(emptyList())
        // When:  prepareChart() is called (selectedRowChartVariant = YEAR)
        // Then:  chartData.value is empty
    }

    // -------------------------------------------------------------------------
    // MONTHLY mode — monthlyPlaysTimeData()
    // Triggered when selectedRowChartVariant == MONTHLY
    // Always produces 12 bars (Jan–Dec), even for months with 0 games
    // Filters by gameData.year == state.selectedYear
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_monthlyMode_alwaysReturns12Bars() {
        // Given: history with games only in January and March
        //        state.selectedYear = 2024
        //        state.selectedRowChartVariant = RowChartVariantsEnum.MONTHLY
        // When:  prepareChart() is called
        // Then:  chartData has exactly 12 bars (one per month)
        //        bar labels match Months.monthsName values ("Jan", "Feb", ..., "Dec")
    }

    @Test
    fun prepareChart_monthlyMode_countIsCorrectPerMonth() {
        // Given: history = 3 games in January 2024, 1 game in March 2024, 2 games in March 2023
        //        state.selectedYear = 2024
        // When:  prepareChart() is called
        // Then:  chartData["Jan"].value == 3.0
        //        chartData["Mar"].value == 1.0  (2023 games excluded by year filter)
        //        all other months value  == 0.0
    }

    @Test
    fun prepareChart_monthlyMode_ignoresGamesFromOtherYears() {
        // Given: history has 5 games all in January 2023, state.selectedYear = 2024
        // When:  prepareChart() is called
        // Then:  all 12 bars have value 0.0 (2023 games are outside selectedYear)
    }

    // -------------------------------------------------------------------------
    // MONTH mode — monthPlaysTimeData()
    // Triggered when selectedRowChartVariant == MONTH
    // Only produces bars for days that have at least 1 game
    // Filters by gameData.year == selectedYear AND gameData.monthValue == selectedMonth
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_monthMode_onlyDaysWithGamesGetBars() {
        // Given: history = 2 games on 2024-06-05, 1 game on 2024-06-20
        //        state.selectedYear = 2024, state.selectedMonth = 6
        //        state.selectedRowChartVariant = RowChartVariantsEnum.MONTH
        // When:  prepareChart() is called
        // Then:  chartData has exactly 2 bars
        //        bar with label "5"  has value 2.0
        //        bar with label "20" has value 1.0
    }

    @Test
    fun prepareChart_monthMode_noDaysWithGames_chartDataIsEmpty() {
        // Given: history games are all in a different month/year
        //        state.selectedYear = 2024, state.selectedMonth = 6
        // When:  prepareChart() is called
        // Then:  chartData.value is empty (no bars generated for days with 0 games)
    }

    @Test
    fun prepareChart_monthMode_respectsBothYearAndMonthFilter() {
        // Given: 3 games on 2024-06-10, 2 games on 2023-06-10 (different year)
        //        state.selectedYear = 2024, state.selectedMonth = 6
        // When:  prepareChart() is called
        // Then:  chartData has 1 bar with label "10" and value 3.0
        //        the 2 games from 2023 are excluded
    }

    // -------------------------------------------------------------------------
    // PERIOD mode — periodPlaysTimeData()
    // Triggered when selectedRowChartVariant == PERIOD
    // Generates a date sequence from startDate to endDate (inclusive)
    // Only dates with ≥1 game get a bar; label is the date's toString() (ISO-8601)
    // -------------------------------------------------------------------------

    @Test
    fun prepareChart_periodMode_gameOnDateInRange_getsBar() {
        // Given: history = 1 game on 2024-06-10
        //        state.startDate = LocalDate.of(2024, 6, 8)
        //        state.endDate   = LocalDate.of(2024, 6, 12)
        //        state.selectedRowChartVariant = RowChartVariantsEnum.PERIOD
        // When:  prepareChart() is called
        // Then:  chartData has 1 bar
        //        bar.label == "2024-06-10"
        //        bar.values[0].value == 1.0
    }

    @Test
    fun prepareChart_periodMode_gameOutsideRange_notIncluded() {
        // Given: history = 1 game on 2024-06-01 (before range), 1 game on 2024-06-20 (after)
        //        state.startDate = LocalDate.of(2024, 6, 5)
        //        state.endDate   = LocalDate.of(2024, 6, 15)
        // When:  prepareChart() is called
        // Then:  chartData.value is empty (both games are outside the range)
    }

    @Test
    fun prepareChart_periodMode_startDateEqualsEndDate_singleDayRange() {
        // Given: history = 2 games on 2024-06-10
        //        startDate = endDate = LocalDate.of(2024, 6, 10)
        // When:  prepareChart() is called
        // Then:  chartData has 1 bar with label "2024-06-10" and value 2.0
    }

    @Test
    fun prepareChart_periodMode_multipleGamesOnSameDay_singleBarWithCorrectCount() {
        // Given: history = 3 games on 2024-06-10, 2 games on 2024-06-11
        //        startDate = LocalDate.of(2024, 6, 10), endDate = LocalDate.of(2024, 6, 11)
        // When:  prepareChart() is called
        // Then:  chartData has 2 bars
        //        "2024-06-10" has value 3.0
        //        "2024-06-11" has value 2.0
    }

    // -------------------------------------------------------------------------
    // playGamesAllTimeData() — called from init, result in rowChartData
    // Shows each game's share of total plays as a ratio (game.games / total)
    // -------------------------------------------------------------------------

    @Test
    fun init_playGamesAllTimeData_correctProportionPerGame() {
        // Given: allGameUseCase.invoke() returns [game(games=3), game(games=1)]
        //        total = 4
        // When:  ViewModel is constructed (init runs playGamesAllTimeData())
        // Then:  rowChartData has 2 bars
        //        bar[0].values[0].value == 0.75   (3/4)
        //        bar[1].values[0].value == 0.25   (1/4)
    }

    @Test
    fun init_playGamesAllTimeData_emptyGameList_rowChartDataIsEmpty() {
        // Given: allGameUseCase.invoke() returns emptyList()
        // When:  ViewModel is constructed
        // Then:  rowChartData.value is empty
        //        (no division-by-zero: sumOf on empty list = 0, forEach on empty list = no-op)
    }

    @Test
    fun init_playGamesAllTimeData_allGamesHaveZeroPlays_ratioIsNaN() {
        // Given: allGameUseCase.invoke() returns [game(games=0), game(games=0)]
        //        allGamesCount = 0, so 0.0 / 0 = Double.NaN
        // When:  ViewModel is constructed
        // Then:  rowChartData[0].values[0].value is Double.NaN  (or document expected behaviour)
        // Note:  this is a KNOWN EDGE CASE in the current implementation —
        //        the test documents the actual behaviour; consider adding a guard in the VM.
    }
}
