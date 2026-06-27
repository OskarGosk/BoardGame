package com.goskar.boardgame.ui.gamesList.play

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class GamePlayViewModelTest {

    private lateinit var playerRepo: PlayerDbRepository
    private lateinit var gameRepo: GameDbRepository
    private lateinit var historyRepo: GamesHistoryDbRepository
    private lateinit var useCase: GetAllGameUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: GamePlayViewModel

    // Test Data
    private val chess = createGame(name = "Chess", games = 8)
    private val azul = createGame(name = "Azul", games = 2)
    private val wingspan = createGame(name = "Wingspan", games = 5)
    private val wingspanExpansion =
        createGame(name = "Wingspan: European Expansion", expansion = true, games = 4, baseGameId = "Wingspan")
    private val azulExpansion =
        createGame(name = "Azul And Dark Knight", expansion = true, games = 1, baseGameId = "Azul")

    private fun createGame(
        name: String,
        expansion: Boolean = false,
        games: Int = 0,
        id: String = name,
        baseGameId: String? = null
    ) = Game(
        name = name,
        expansion = expansion,
        cooperate = false,
        baseGame = "",
        baseGameId = baseGameId,
        minPlayer = "1",
        maxPlayer = "4",
        games = games,
        id = id
    )

    val player1 = Player(
        name = "Alice",
        id = "id-1",
        games = 2,
        winRatio = 1,
        description = "Test",
        selected = false
    )
    val player2 = Player(
        name = "Bob",
        id = "id-2",
        games = 3,
        winRatio = 2,
        description = "Test",
        selected = false
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        playerRepo = mockk()
        gameRepo = mockk()
        historyRepo = mockk()
        useCase = mockk()

        coEvery { useCase.invoke() } returns listOf(
            chess,
            azul,
            wingspan,
            wingspanExpansion,
            azulExpansion
        )

        viewModel = GamePlayViewModel(
            playerDbRepository = playerRepo,
            gameDbRepository = gameRepo,
            gamesHistoryDbRepository = historyRepo,
            getAllGameUseCase = useCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // selectedPlayer(player)
    // -------------------------------------------------------------------------

    @Test
    fun selectedPlayer_unselectedPlayer_becomesSelected() = runTest(testDispatcher) {
        coEvery { playerRepo.getAllPlayer() } returns RequestResult.Success(listOf(player1, player2))

        viewModel.state.test {
            skipItems(1)
            viewModel.getAllPlayer()
            assertEquals(listOf(player1, player2), awaitItem().playerList)

            viewModel.selectedPlayer(player1)

            val finalItem = awaitItem()
            assertEquals(1, finalItem.countSelectedPlayer)
            assertEquals(true, finalItem.playerList?.find { it.id == "id-1" }?.selected)
        }
    }

    @Test
    fun selectedPlayer_selectedPlayer_becomesDeselected() = runTest(testDispatcher) {
        val player1selected = player1.copy(selected = true)
        coEvery { playerRepo.getAllPlayer() } returns RequestResult.Success(listOf(player1selected, player2))

        viewModel.state.test {
            skipItems(1)
            viewModel.getAllPlayer()
            assertEquals(true, awaitItem().playerList?.find { it.id == "id-1" }?.selected)

            viewModel.selectedPlayer(player1)

            val finalItem = awaitItem()
            assertEquals(0, finalItem.countSelectedPlayer)
            assertEquals(false, finalItem.playerList?.find { it.id == "id-1" }?.selected)
        }
    }

    // -------------------------------------------------------------------------
    // selectExpansion(expansionId)
    // -------------------------------------------------------------------------

    @Test
    fun selectExpansion_unselectedExpansion_becomesSelected() = runTest(testDispatcher) {
        val exp1 = ExpansionGameUiState(createGame(name = "Exp 1", id = "exp-1", expansion = true), isSelected = false)
        viewModel.update(viewModel.state.value.copy(gameList = listOf(exp1)))

        viewModel.state.test {
            assertEquals(false, awaitItem().gameList?.get(0)?.isSelected)

            viewModel.selectExpansion("exp-1")

            val finalItem = awaitItem()
            assertEquals(true, finalItem.gameList?.get(0)?.isSelected)
        }
    }

    @Test
    fun selectExpansion_selectedExpansion_becomesDeselected() = runTest(testDispatcher) {
        val exp1 = ExpansionGameUiState(createGame(name = "Exp 1", id = "exp-1", expansion = true), isSelected = true)
        viewModel.update(viewModel.state.value.copy(gameList = listOf(exp1)))

        viewModel.state.test {
            assertEquals(true, awaitItem().gameList?.get(0)?.isSelected)

            viewModel.selectExpansion("exp-1")

            val finalItem = awaitItem()
            assertEquals(false, finalItem.gameList?.get(0)?.isSelected)
        }
    }

    // -------------------------------------------------------------------------
    // setGameVariant()
    // -------------------------------------------------------------------------

    @Test
    fun setGameVariant_cooperateGame_setsCoopVariant() = runTest(testDispatcher) {
        val coopGame = chess.copy(cooperate = true)
        viewModel.update(viewModel.state.value.copy(game = coopGame))

        viewModel.setGameVariant()

        assertEquals(R.string.history_coop, viewModel.state.value.gameVariant)
    }

    @Test
    fun setGameVariant_nonCooperateGame_setsNormalVariant() = runTest(testDispatcher) {
        val normalGame = chess.copy(cooperate = false)
        viewModel.update(viewModel.state.value.copy(game = normalGame))

        viewModel.setGameVariant()

        assertEquals(R.string.history_normal, viewModel.state.value.gameVariant)
    }

    // -------------------------------------------------------------------------
    // getAllGame() + setGameData()
    // -------------------------------------------------------------------------

    @Test
    fun getAllGame_baseGame_gameListFilteredToDirectExpansions() = runTest(testDispatcher) {
        viewModel.update(viewModel.state.value.copy(game = wingspan))

        viewModel.getAllGame()

        val list = viewModel.state.value.gameList ?: emptyList()
        assertEquals(1, list.size)
        assertEquals("Wingspan: European Expansion", list[0].game.name)
        assertEquals(false, list[0].isSelected)
    }

    @Test
    fun getAllGame_expansionGame_resolvesToBaseAndAutoSelectsOriginalExpansion() = runTest(testDispatcher) {
        // User starts with an expansion
        viewModel.update(viewModel.state.value.copy(game = wingspanExpansion))

        viewModel.getAllGame()

        val state = viewModel.state.value
        assertEquals("Wingspan", state.game?.name) // Resolved to base
        val list = state.gameList ?: emptyList()
        assertEquals(1, list.size)
        assertEquals("Wingspan: European Expansion", list[0].game.name)
        assertEquals(true, list[0].isSelected) // Auto-selected original
    }

    // -------------------------------------------------------------------------
    // getAllPlayer()
    // -------------------------------------------------------------------------

    @Test
    fun getAllPlayer_success_setsPlayerListAndClearsError() = runTest(testDispatcher) {
        coEvery { playerRepo.getAllPlayer() } returns RequestResult.Success(listOf(player1, player2))

        viewModel.getAllPlayer()

        assertEquals(listOf(player1, player2), viewModel.state.value.playerList)
    }

    @Test
    fun getAllPlayer_error_showsError() = runTest(testDispatcher) {
        coEvery { playerRepo.getAllPlayer() } returns RequestResult.Error(Throwable())

        viewModel.events.test {
            viewModel.getAllPlayer()
            assertEquals(
                GamePlayEvent.ShowMessage(R.string.error_generic, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
    }
}
