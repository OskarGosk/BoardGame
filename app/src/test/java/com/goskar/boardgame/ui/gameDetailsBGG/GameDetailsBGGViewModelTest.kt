package com.goskar.boardgame.ui.gameDetailsBGG

import com.goskar.boardgame.data.models.BoardGameBGG
import com.goskar.boardgame.data.models.BoardGamesDetails
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.bbg.BoardGameApiRepository
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import io.mockk.coEvery
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameDetailsBGGViewModelTest {

    private lateinit var apiRepo: BoardGameApiRepository
    private lateinit var gameRepo: GameDbRepository
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: GameDetailsBGGViewModel

    private val baseGame = createGame(name = "Wingspan", id = "base-1", expansion = false)
    private val expansionGame = createGame(name = "Wingspan: Europe", id = "exp-1", expansion = true)

    private fun createGame(name: String, id: String, expansion: Boolean) = Game(
        name = name, expansion = expansion, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, id = id
    )

    private fun details(minPlayers: Int?, maxPlayers: Int?, image: String?) = BoardGamesDetails(
        boardGamesBGG = listOf(
            BoardGameBGG(id = "1", minPlayers = minPlayers, maxPlayers = maxPlayers, image = image)
        )
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        apiRepo = mockk()
        gameRepo = mockk()
        getAllGameUseCase = mockk()

        coEvery { getAllGameUseCase.invoke() } returns listOf(baseGame, expansionGame)

        viewModel = GameDetailsBGGViewModel(apiRepo, gameRepo, getAllGameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // init — base game list excludes expansions
    // -------------------------------------------------------------------------

    @Test
    fun init_allBaseGame_excludesExpansions() {
        assertEquals(listOf(baseGame), viewModel.allBaseGame.value)
    }

    // -------------------------------------------------------------------------
    // getGame()
    // -------------------------------------------------------------------------

    @Test
    fun getGame_success_populatesGameDetailsAndClearsLoading() = runTest(testDispatcher) {
        val data = details(minPlayers = 2, maxPlayers = 5, image = "img")
        coEvery { apiRepo.getGame(any()) } returns RequestResult.Success(data)

        viewModel.getGame()

        assertEquals(data, viewModel.gameDetails.value)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(false, viewModel.state.value.isError)
    }

    @Test
    fun getGame_error_setsIsErrorAndClearsLoading() = runTest(testDispatcher) {
        coEvery { apiRepo.getGame(any()) } returns RequestResult.Error(Throwable("network"))

        viewModel.getGame()

        assertEquals(true, viewModel.state.value.isError)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    // -------------------------------------------------------------------------
    // validateAddGame() — maps BGG details into the persisted Game
    // -------------------------------------------------------------------------

    @Test
    fun validateAddGame_mapsBggDetailsAndStateIntoGame() = runTest(testDispatcher) {
        coEvery { apiRepo.getGame(any()) } returns
            RequestResult.Success(details(minPlayers = 2, maxPlayers = 5, image = "http://img"))
        val saved = slot<Game>()
        coEvery { gameRepo.insertGame(capture(saved)) } returns RequestResult.Success(true)

        viewModel.getGame() // populates gameDetails
        viewModel.update(
            viewModel.state.value.copy(
                gameName = "Wingspan",
                expansion = true,
                cooperate = true,
                baseGame = "Base",
                baseGameId = "base-1"
            )
        )
        viewModel.validateAddGame()

        with(saved.captured) {
            assertEquals("Wingspan", name)
            assertEquals("2", minPlayer)
            assertEquals("5", maxPlayer)
            assertEquals("http://img", uriFromBgg)
            assertEquals(true, expansion)
            assertEquals(true, cooperate)
            assertEquals("Base", baseGame)
            assertEquals("base-1", baseGameId)
        }
    }

    @Test
    fun validateAddGame_noDetailsLoaded_usesZeroPlayerFallbacks() = runTest(testDispatcher) {
        val saved = slot<Game>()
        coEvery { gameRepo.insertGame(capture(saved)) } returns RequestResult.Success(true)

        viewModel.update(viewModel.state.value.copy(gameName = "Mystery"))
        viewModel.validateAddGame()

        with(saved.captured) {
            assertEquals("Mystery", name)
            assertEquals("0", minPlayer)
            assertEquals("0", maxPlayer)
            assertNull(uriFromBgg)
        }
    }

    // -------------------------------------------------------------------------
    // validateAddGame() — result handling
    // -------------------------------------------------------------------------

    @Test
    fun validateAddGame_success_setsSuccessFlagAndClearsLoading() = runTest(testDispatcher) {
        coEvery { gameRepo.insertGame(any()) } returns RequestResult.Success(true)

        viewModel.update(viewModel.state.value.copy(gameName = "Chess"))
        viewModel.validateAddGame()

        assertEquals(true, viewModel.state.value.successAddEditGame)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun validateAddGame_error_setsIsErrorAndClearsLoading() = runTest(testDispatcher) {
        coEvery { gameRepo.insertGame(any()) } returns RequestResult.Error(Throwable("db error"))

        viewModel.update(viewModel.state.value.copy(gameName = "Chess"))
        viewModel.validateAddGame()

        assertEquals(false, viewModel.state.value.successAddEditGame)
        assertEquals(true, viewModel.state.value.isError)
        assertEquals(false, viewModel.state.value.isLoading)
    }
}
