package com.goskar.boardgame.ui.screens.gameDetails.viewmodel

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
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameDetailsNewViewModelTest {

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

    private fun buildViewModel() = GameDetailsNewViewModel(api, gameDbRepository)

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
        assertEquals("1–4 players", state.players)
        assertEquals("40 min", state.playtime)
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
}
