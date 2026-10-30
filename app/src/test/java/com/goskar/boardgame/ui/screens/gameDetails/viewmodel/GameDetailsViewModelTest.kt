package com.goskar.boardgame.ui.screens.gameDetails.viewmodel

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.GameMode
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import io.mockk.CapturingSlot
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertNull
import com.goskar.boardgame.data.models.BggCategory
import com.goskar.boardgame.data.models.BggDesigner
import com.goskar.boardgame.data.models.BggMechanic
import com.goskar.boardgame.data.models.BggPollResult
import com.goskar.boardgame.data.models.BggPollSummary
import com.goskar.boardgame.data.models.BggPublisher
import com.goskar.boardgame.data.models.BggRatings
import com.goskar.boardgame.data.models.BggStatistics
import com.goskar.boardgame.data.models.BoardGameBGG
import com.goskar.boardgame.data.models.BoardGamesDetails
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.bgg.BoardGameApiRepository
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameDetailsViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var api: BoardGameApiRepository
    private lateinit var gameDbRepository: GameDbRepository

    private val marvelUnited = BoardGameBGG(
        id = "298047",
        yearPublished = 2020,
        minPlayers = 1,
        maxPlayers = 4,
        playingTime = 40,
        minPlayTime = 40,
        maxPlayTime = 40,
        age = 14,
        thumbnail = "thumb.jpg",
        image = "image.jpg",
        description = "Cooperative &amp;rsquo;superhero&amp;rsquo; game.",
        categories = listOf(BggCategory(value = "Card Game")),
        mechanics = listOf(BggMechanic(value = "Cooperative Game"), BggMechanic(value = "Hand Management")),
        designers = listOf(BggDesigner(value = "Eric M. Lang")),
        publishers = listOf(BggPublisher(value = "CMON")),
        pollSummaries = listOf(
            BggPollSummary(
                name = "suggested_numplayers",
                results = listOf(BggPollResult(name = "bestwith", value = "Best with 3 players")),
            )
        ),
        statistics = BggStatistics(ratings = BggRatings(average = 7.5, averageWeight = 1.8)),
    )

    private fun buildViewModel() = GameDetailsViewModel(api, gameDbRepository)

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        gameDbRepository = mockk()
        coEvery { gameDbRepository.getAllGame() } returns RequestResult.Success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_mapsBggDetailsToState() = runTest(testDispatcher) {
        coEvery { api.getGame("298047") } returns RequestResult.Success(BoardGamesDetails(listOf(marvelUnited)))

        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United")

        val state = viewModel.state.value
        assertEquals("Marvel United", state.name)
        assertEquals("2020", state.year)
        assertEquals("14+", state.age)
        assertEquals("7.5", state.rating)
        assertEquals("1.8 / 5", state.weight)
        assertEquals("Best with 3 players", state.bestPlayers)
        assertTrue(state.cooperate)
        assertEquals(listOf("Card Game"), state.categories)
        assertEquals(listOf("Cooperative Game", "Hand Management"), state.mechanics)
        assertEquals(listOf("Eric M. Lang"), state.designers)
        assertTrue(state.description.contains("superhero"))
    }

    @Test
    fun load_error_setsErrorState() = runTest(testDispatcher) {
        coEvery { api.getGame(any()) } returns RequestResult.Error(Throwable("offline"))

        val viewModel = buildViewModel()
        viewModel.load("1", "X")

        assertTrue(viewModel.state.value.isError)
    }

    @Test
    fun addToCollection_savesGameWithBggFields() = runTest(testDispatcher) {
        coEvery { api.getGame("298047") } returns RequestResult.Success(BoardGamesDetails(listOf(marvelUnited)))
        val saved = slot<Game>()
        coEvery { gameDbRepository.insertGame(capture(saved)) } returns RequestResult.Success(true)

        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United")
        viewModel.addToCollection()

        coVerify(exactly = 1) { gameDbRepository.insertGame(any()) }
        val game = saved.captured
        assertEquals("Marvel United", game.name)
        assertTrue(game.cooperate)
        assertEquals("Card Game", game.category)
        assertEquals(2020, game.yearPublished)
        assertEquals(40, game.playTime)
        assertEquals(7.5, game.rating!!, 0.0001)
        assertEquals("1", game.minPlayer)
        assertEquals("4", game.maxPlayer)
    }

    private val oldImport = Game(
        name = "Marvel United PL",
        expansion = false,
        cooperate = true,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "",
        uriFromBgg = "old-cover.jpg",
        games = 12,
        category = "Kooperacja",
        modes = listOf(GameMode.COOPERATIVE),
        id = "local-1",
    )

    private fun linkSetup(game: Game): CapturingSlot<Game> {
        coEvery { api.getGame("298047") } returns RequestResult.Success(BoardGamesDetails(listOf(marvelUnited)))
        coEvery { gameDbRepository.getAllGame() } returns RequestResult.Success(listOf(game))
        val saved = slot<Game>()
        coEvery { gameDbRepository.editGame(capture(saved)) } returns RequestResult.Success(true)
        return saved
    }

    @Test
    fun load_linkMode_exposesCollectionGameName() = runTest(testDispatcher) {
        linkSetup(oldImport)

        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United", linkGameId = "local-1")

        assertEquals("Marvel United PL", viewModel.state.value.linkGameName)
    }

    @Test
    fun load_withoutLinkGame_isNotLinkMode() = runTest(testDispatcher) {
        coEvery { api.getGame("298047") } returns RequestResult.Success(BoardGamesDetails(listOf(marvelUnited)))

        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United")

        assertNull(viewModel.state.value.linkGameName)
    }

    @Test
    fun linkToGame_setsBggIdAndFillsOnlyEmptyFields() = runTest(testDispatcher) {
        val saved = linkSetup(oldImport)
        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United", linkGameId = "local-1")

        viewModel.events.test {
            viewModel.linkToGame()
            assertEquals(
                GameDetailsEvent.Linked(R.string.gd_linked, AppSnackBarType.SUCCESS, "298047", "Marvel United PL"),
                awaitItem()
            )
        }

        val game = saved.captured
        assertEquals("298047", game.bggId)
        assertEquals("local-1", game.id)
        assertEquals("Marvel United PL", game.name)
        assertEquals("old-cover.jpg", game.uriFromBgg)
        assertEquals(12, game.games)
        assertEquals(listOf(GameMode.COOPERATIVE), game.modes)
        assertEquals("Kooperacja", game.category)
        assertEquals("1", game.minPlayer)
        assertEquals("4", game.maxPlayer)
        assertEquals(2020, game.yearPublished)
        assertEquals(40, game.playTime)
        assertEquals(7.5, game.rating!!, 0.0001)
    }

    @Test
    fun linkToGame_manualGameWithoutCover_getsBggCover() = runTest(testDispatcher) {
        val saved = linkSetup(oldImport.copy(uriFromBgg = null, uri = null))
        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United", linkGameId = "local-1")

        viewModel.linkToGame()

        assertEquals("image.jpg", saved.captured.uriFromBgg)
    }

    @Test
    fun linkToGame_manualGameWithOwnPhoto_keepsItWithoutBggCover() = runTest(testDispatcher) {
        val saved = linkSetup(oldImport.copy(uriFromBgg = null, uri = "content://photo"))
        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United", linkGameId = "local-1")

        viewModel.linkToGame()

        assertEquals("content://photo", saved.captured.uri)
        assertNull(saved.captured.uriFromBgg)
    }

    @Test
    fun linkToGame_saveFails_showsErrorAndAllowsRetry() = runTest(testDispatcher) {
        linkSetup(oldImport)
        coEvery { gameDbRepository.editGame(any()) } returns RequestResult.Error(Throwable("db"))
        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United", linkGameId = "local-1")

        viewModel.events.test {
            viewModel.linkToGame()
            assertEquals(GameDetailsEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR), awaitItem())
        }
        assertEquals(false, viewModel.state.value.isLinking)
    }

    @Test
    fun linkToGame_gameDeletedMeanwhile_showsErrorWithoutSaving() = runTest(testDispatcher) {
        linkSetup(oldImport)
        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United", linkGameId = "local-1")
        coEvery { gameDbRepository.getAllGame() } returns RequestResult.Success(emptyList())

        viewModel.events.test {
            viewModel.linkToGame()
            assertEquals(GameDetailsEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR), awaitItem())
        }
        coVerify(exactly = 0) { gameDbRepository.editGame(any()) }
    }

    @Test
    fun linkToGame_doubleTap_savesOnce() = runTest(testDispatcher) {
        linkSetup(oldImport)
        val release = CompletableDeferred<Unit>()
        coEvery { gameDbRepository.editGame(any()) } coAnswers {
            release.await()
            RequestResult.Success(true)
        }
        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United", linkGameId = "local-1")

        viewModel.linkToGame()
        viewModel.linkToGame()
        release.complete(Unit)

        coVerify(exactly = 1) { gameDbRepository.editGame(any()) }
    }

    @Test
    fun linkToGame_notInLinkMode_doesNothing() = runTest(testDispatcher) {
        linkSetup(oldImport)
        val viewModel = buildViewModel()
        viewModel.load("298047", "Marvel United")

        viewModel.linkToGame()

        coVerify(exactly = 0) { gameDbRepository.editGame(any()) }
    }

    @Ignore("TODO: sprawdz, ze state.players to '1–4', a state.playtime to '40' (same liczby, jednostke 'min' dodaje UI przez zasob gd_minutes_value)")
    @Test
    fun load_mapsPlayersAndPlaytimeToPlainNumbers() {
    }
}
