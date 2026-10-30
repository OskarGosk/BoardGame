package com.goskar.boardgame.ui.screens.history.viewmodel

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.CoopWinner
import com.goskar.boardgame.data.models.GameMode
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetHistoryWithExpansionUseCase
import com.goskar.boardgame.data.useCase.HistoryGameWithExpansion
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GamesHistoryViewModelTest {

    private lateinit var repo: GamesHistoryDbRepository
    private lateinit var useCase: GetHistoryWithExpansionUseCase
    private lateinit var gameUseCase: GetAllGameUseCase
    private lateinit var userSession: UserRepository
    private lateinit var playerRepo: PlayerDbRepository
    private lateinit var mePlayerRepo: MePlayerRepository
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

    private fun newViewModel() =
        GamesHistoryViewModel(repo, useCase, gameUseCase, userSession, playerRepo, mePlayerRepo)

    private fun loggedInAs(playerName: String) {
        val me = Player(name = playerName, games = 0, winRatio = 0, description = "", selected = false, id = "me")
        coEvery { userSession.getCurrentSession() } returns User(email = "a@b.c", token = "t", userUID = "uid1")
        coEvery { mePlayerRepo.getLinkedPlayerId("uid1") } returns "me"
        coEvery { playerRepo.getAllPlayer() } returns RequestResult.Success(listOf(me))
    }

    private fun session(
        name: String,
        winner: String,
        players: List<String> = listOf("Alex", "Sam"),
        mode: GameMode? = null,
        teams: Map<String, Int>? = null,
        winningTeam: Int? = null,
    ) = HistoryGame(
        gameName = name,
        winner = winner,
        gameData = LocalDate.now(),
        listOfPlayer = players,
        description = "",
        mode = mode,
        teams = teams,
        winningTeam = winningTeam,
        id = name,
    )

    private fun shownGameNames() = viewModel.state.value.groups.flatMap { g -> g.sessions.map { it.gameName } }.sorted()

    // `state` is derived via stateIn(WhileSubscribed), so it only emits while collected.
    private fun TestScope.collectState() {
        backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
    }

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        useCase = mockk()
        gameUseCase = mockk()
        userSession = mockk()
        playerRepo = mockk()
        mePlayerRepo = mockk()

        // Defaults so the init{} block (which calls all loaders) succeeds quietly.
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(emptyList())
        coEvery { useCase.invoke() } returns RequestResult.Success(emptyList())
        coEvery { gameUseCase.invoke() } returns emptyList()
        coEvery { userSession.getCurrentSession() } returns User(email = null, token = null, userUID = "guest")

        viewModel = newViewModel()
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
        collectState()

        val state = viewModel.state.value
        assertEquals(listOf(jan, feb, mar), state.historyList)
        assertEquals(false, state.loading)
    }

    @Test
    fun getAllHistoryGame_error_showsErrorAndClearsLoading() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Error(Throwable("db error"))

        viewModel.events.test {
            viewModel.getAllHistoryGame()
            assertEquals(
                GameHistoryEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
        collectState()
        assertEquals(false, viewModel.state.value.loading)
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

        viewModel = newViewModel()
        collectState()

        assertEquals(listOf(b, a, c), viewModel.state.value.historyGameWithExpansion)
    }

    @Test
    fun init_historyWithExpansionError_showsError() = runTest(testDispatcher) {
        coEvery { useCase.invoke() } returns RequestResult.Error(Throwable("expansion error"))

        viewModel = newViewModel()

        viewModel.events.test {
            assertEquals(
                GameHistoryEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
    }

    // -------------------------------------------------------------------------
    // derived state: groups / showingLabel / search
    // -------------------------------------------------------------------------

    @Test
    fun groups_areBucketedByAgeAndOrderedNewestFirst() = runTest(testDispatcher) {
        val today = LocalDate.now()
        val sessions = listOf(
            historyGame("Old", today.minusDays(30)),
            historyGame("Today", today),
            historyGame("Week", today.minusDays(3)),
            historyGame("Yesterday", today.minusDays(1)),
        )
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(sessions)

        viewModel = newViewModel()
        collectState()

        val groups = viewModel.state.value.groups
        assertEquals(
            listOf("Today", "Yesterday", "Week", "Old"),
            groups.flatMap { g -> g.sessions.map { it.gameName } }
        )
    }

    @Test
    fun search_filtersSessionsByGameName() = runTest(testDispatcher) {
        val today = LocalDate.now()
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(historyGame("Wingspan", today), historyGame("Root", today))
        )

        viewModel = newViewModel()
        collectState()

        viewModel.updateSearchTxt("wing")
        assertEquals(listOf("Wingspan"), viewModel.state.value.groups.flatMap { g -> g.sessions.map { it.gameName } })
    }

    @Ignore("TODO: sprawdz, ze groups.map { it.bucket } to TODAY, YESTERDAY, THIS_WEEK, EARLIER dla sesji z dzis, wczoraj, 3 dni temu i 30 dni temu")
    @Test
    fun groups_areOrderedByBucket() {
    }

    @Ignore("TODO: sprawdz, ze dla jednej starej sesji (60 dni) groups zawiera tylko jedna grupe z bucket EARLIER")
    @Test
    fun groups_skipEmptyBuckets() {
    }

    @Ignore("TODO: sprawdz, ze showingCount to 2 dla dwoch sesji, a po wyszukaniu 'wing' spada do 1")
    @Test
    fun showingCount_reflectsSearch() {
    }

    // -------------------------------------------------------------------------
    // filters
    // -------------------------------------------------------------------------

    @Test
    fun filters_guest_hidesWinsOnly() = runTest(testDispatcher) {
        collectState()

        assertEquals(
            listOf(HistoryFilter.ALL, HistoryFilter.COOP, HistoryFilter.TEAMS),
            viewModel.state.value.filters
        )
    }

    @Test
    fun filters_accountWithoutLinkedPlayer_hidesWinsOnly() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns User(email = "a@b.c", token = "t", userUID = "uid1")
        coEvery { mePlayerRepo.getLinkedPlayerId("uid1") } returns null

        viewModel = newViewModel()
        collectState()

        assertEquals(false, HistoryFilter.WINS_ONLY in viewModel.state.value.filters)
    }

    @Test
    fun filters_linkedPlayer_exposesAllFiltersInOrder() = runTest(testDispatcher) {
        loggedInAs("Alex")

        viewModel = newViewModel()
        collectState()

        assertEquals(HistoryFilter.entries, viewModel.state.value.filters)
    }

    @Test
    fun winsOnly_competitive_keepsOnlySessionsWonByMe() = runTest(testDispatcher) {
        loggedInAs("Alex")
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(session("Won", winner = "alex"), session("Lost", winner = "Sam"))
        )

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.WINS_ONLY)

        assertEquals(listOf("Won"), shownGameNames())
        assertEquals(1, viewModel.state.value.showingCount)
    }

    @Test
    fun winsOnly_coop_countsOnlyWinsIPlayedIn() = runTest(testDispatcher) {
        loggedInAs("Alex")
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(
                session("CoopWon", winner = CoopWinner.PLAYERS, mode = GameMode.COOPERATIVE),
                session("CoopLost", winner = CoopWinner.COMPUTER, mode = GameMode.COOPERATIVE),
                session("CoopWithoutMe", winner = CoopWinner.PLAYERS, players = listOf("Sam", "Kim"), mode = GameMode.COOPERATIVE),
            )
        )

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.WINS_ONLY)

        assertEquals(listOf("CoopWon"), shownGameNames())
    }

    @Test
    fun winsOnly_team_keepsSessionsWonByMyTeam() = runTest(testDispatcher) {
        loggedInAs("Alex")
        val teams = mapOf("Alex" to 1, "Sam" to 2)
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(
                session("TeamWon", winner = "", mode = GameMode.TEAM, teams = teams, winningTeam = 1),
                session("TeamLost", winner = "", mode = GameMode.TEAM, teams = teams, winningTeam = 2),
            )
        )

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.WINS_ONLY)

        assertEquals(listOf("TeamWon"), shownGameNames())
    }

    @Test
    fun coop_includesLegacySessionsWithoutMode() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(
                session("LegacyCoopWin", winner = CoopWinner.PLAYERS),
                session("LegacyCoopLoss", winner = CoopWinner.COMPUTER),
                session("Coop", winner = CoopWinner.PLAYERS, mode = GameMode.COOPERATIVE),
                session("Competitive", winner = "Alex"),
                session("Team", winner = "", mode = GameMode.TEAM),
            )
        )

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.COOP)

        assertEquals(listOf("Coop", "LegacyCoopLoss", "LegacyCoopWin"), shownGameNames())
    }

    @Test
    fun teams_keepsOnlyTeamSessions() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(
                session("Team", winner = "", mode = GameMode.TEAM),
                session("Coop", winner = CoopWinner.PLAYERS, mode = GameMode.COOPERATIVE),
                session("Competitive", winner = "Alex"),
            )
        )

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.TEAMS)

        assertEquals(listOf("Team"), shownGameNames())
    }

    @Test
    fun filter_combinesWithSearch() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(
                session("Spirit Island", winner = CoopWinner.PLAYERS, mode = GameMode.COOPERATIVE),
                session("Pandemic", winner = CoopWinner.PLAYERS, mode = GameMode.COOPERATIVE),
                session("Spirits of the Forest", winner = "Alex"),
            )
        )

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.COOP)
        viewModel.updateSearchTxt("spirit")

        assertEquals(listOf("Spirit Island"), shownGameNames())
    }

    @Test
    fun all_afterAnotherFilter_showsEverySession() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(
            listOf(session("Team", winner = "", mode = GameMode.TEAM), session("Competitive", winner = "Alex"))
        )

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.TEAMS)
        viewModel.updateSelectedFilter(HistoryFilter.ALL)

        assertEquals(listOf("Competitive", "Team"), shownGameNames())
    }

    @Test
    fun winsOnly_withoutLinkedPlayer_fallsBackToAll() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(listOf(session("Any", winner = "Sam")))

        viewModel = newViewModel()
        collectState()
        viewModel.updateSelectedFilter(HistoryFilter.WINS_ONLY)

        assertEquals(HistoryFilter.ALL, viewModel.state.value.selectedFilter)
        assertEquals(listOf("Any"), shownGameNames())
    }
}
