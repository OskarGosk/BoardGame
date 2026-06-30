package com.goskar.boardgame.ui.gamesList.lists

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.utils.SortList
import com.goskar.boardgame.data.rest.RequestResult
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
class GameListViewModelTest {

    private lateinit var gameRepo: GameDbRepository
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: GameListViewModel

    // Test Data
    private val chess = createGame(name = "Chess", games = 8)
    private val azul = createGame(name = "Azul", games = 2)
    private val wingspan = createGame(name = "Wingspan", games = 5)
    private val wingspanExpansion =
        createGame(name = "Wingspan: European Expansion", expansion = true, games = 4)
    private val azulExpansion =
        createGame(name = "Azul And Dark Knight", expansion = true, games = 1)

    private val game1Chess = GameUiState(chess, true)
    private val game2Azul = GameUiState(azul, true)
    private val game3Wingspan = GameUiState(wingspan, true)
    private val game4WingspanExpansion = GameUiState(wingspanExpansion, true)
    private val game5AzulExpansion = GameUiState(azulExpansion, true)

    private fun createGame(
        name: String,
        expansion: Boolean = false,
        games: Int = 0,
        id: String = name
    ) = Game(
        name = name,
        expansion = expansion,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = games,
        id = id
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        gameRepo = mockk()

        coEvery { gameRepo.getAllGame() } returns RequestResult.Success(
            listOf(chess, azul, wingspan, wingspanExpansion, azulExpansion)
        )

        viewModel = GameListViewModel(gameDbRepository = gameRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // refreshGameList() — Sorting Options
    // -------------------------------------------------------------------------

    @Test
    fun refreshGameList_defaultSort_preservesOriginalOrder() = runTest(testDispatcher) {
        coEvery { gameRepo.getAllGame() } returns RequestResult.Success(
            listOf(chess, azul, wingspan, wingspanExpansion)
        )
        viewModel.refresh()
        viewModel.refreshGameList()

        viewModel.state.test {
            val finalItems = awaitItem()
            assertEquals(
                listOf(game1Chess, game2Azul, game3Wingspan, game4WingspanExpansion),
                finalItems.gameListEdited
            )
        }
    }

    @Test
    fun refreshGameList_nameAscending_sortsAlphabetically() = runTest(testDispatcher) {
        viewModel.refresh()
        viewModel.updateSortOption(SortList.A_Z)
        viewModel.refreshGameList()

        viewModel.state.test {
            val finalItems = awaitItem()
            // Azul, Azul And Dark Knight, Chess, Wingspan, Wingspan: European Expansion
            assertEquals(
                listOf(
                    game2Azul,
                    game5AzulExpansion,
                    game1Chess,
                    game3Wingspan,
                    game4WingspanExpansion
                ),
                finalItems.gameListEdited
            )
        }
    }

    @Test
    fun refreshGameList_nameDescending_sortsReverseAlphabetically() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1) // skip initial state

            viewModel.updateSortOption(SortList.Z_A)
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(SortList.Z_A, finalItems.sortOption)
            // Z-A: Wingspan: European Expansion, Wingspan, Chess, Azul And Dark Knight, Azul
            assertEquals(
                listOf(
                    game4WingspanExpansion,
                    game3Wingspan,
                    game1Chess,
                    game5AzulExpansion,
                    game2Azul,
                ), finalItems.gameListEdited
            )
        }
    }

    @Test
    fun refreshGameList_playedAscending_sortsByGamesCountLowToHigh() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateSortOption(SortList.GAMES_MIN)
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(SortList.GAMES_MIN, finalItems.sortOption)
            // 1 (Azul Expansion), 2 (Azul), 4 (Wingspan Expansion), 5 (Wingspan), 8 (Chess)
            assertEquals(
                listOf(
                    game5AzulExpansion,
                    game2Azul,
                    game4WingspanExpansion,
                    game3Wingspan,
                    game1Chess
                ), finalItems.gameListEdited
            )
        }
    }

    @Test
    fun refreshGameList_playedDescending_sortsByGamesCountHighToLow() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateSortOption(SortList.GAMES_MAX)
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(SortList.GAMES_MAX, finalItems.sortOption)
            // 8, 5, 4, 2, 1
            assertEquals(
                listOf(
                    game1Chess,
                    game3Wingspan,
                    game4WingspanExpansion,
                    game2Azul,
                    game5AzulExpansion
                ), finalItems.gameListEdited
            )
        }
    }

    // -------------------------------------------------------------------------
    // refreshGameList() — Search Filter
    // -------------------------------------------------------------------------

    @Test
    fun refreshGameList_searchTxtMatches_onlyMatchingGamesInResult() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateSearchTxt("az")
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(listOf(game2Azul, game5AzulExpansion), finalItems.gameListEdited)
        }
    }

    @Test
    fun refreshGameList_searchTxtMatchesNone_resultIsEmpty() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateSearchTxt("zzz")
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(emptyList<GameUiState>(), finalItems.gameListEdited)
        }
    }

    @Test
    fun refreshGameList_searchTxtIsCaseInsensitive() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateSearchTxt("CHESS")
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(listOf(game1Chess), finalItems.gameListEdited)
        }
    }

    @Test
    fun refreshGameList_emptySearchTxt_allGamesPassFilter() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            // 1. Search for specific game
            viewModel.updateSearchTxt("CHESS")
            viewModel.refreshGameList()
            assertEquals(listOf(game1Chess), expectMostRecentItem().gameListEdited)

            // 2. Clear search
            viewModel.updateSearchTxt("")
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(5, finalItems.gameListEdited.size)
        }
    }

    // -------------------------------------------------------------------------
    // refreshGameList() — Checkbox Visibility Filters
    // -------------------------------------------------------------------------

    @Test
    fun refreshGameList_onlyBaseCheckbox_onlyBaseGamesVisible() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateCheckboxExpansionGame() // true -> false
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(listOf(game1Chess, game2Azul, game3Wingspan), finalItems.gameListEdited)
        }
    }

    @Test
    fun refreshGameList_onlyExpansionCheckbox_onlyExpansionsVisible() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateCheckboxBaseGame() // true -> false
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(
                listOf(game4WingspanExpansion, game5AzulExpansion),
                finalItems.gameListEdited
            )
        }
    }

    @Test
    fun refreshGameList_bothCheckboxesFalse_resultIsEmpty() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateCheckboxBaseGame()      // true -> false
            viewModel.updateCheckboxExpansionGame() // true -> false
            viewModel.refreshGameList()

            val finalItems = expectMostRecentItem()
            assertEquals(emptyList<GameUiState>(), finalItems.gameListEdited)
        }
    }

    // -------------------------------------------------------------------------
    // UI Expansion Logic
    // -------------------------------------------------------------------------

    @Test
    fun updateExpandedGameCover_matchingGame_togglesIsExpanded() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateExpandedGameCover(azul)

            val finalItems = expectMostRecentItem()
            assertEquals(false, finalItems.gameList?.find { it.game.name == "Azul" }?.isExpanded)
        }
    }

    @Test
    fun updateExpandedGameCover_onlyTargetGameChanges() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.updateExpandedGameCover(azul)

            val list = expectMostRecentItem().gameList ?: emptyList()
            assertEquals(false, list.find { it.game.id == azul.id }?.isExpanded)
            assertEquals(true, list.find { it.game.id == chess.id }?.isExpanded)
            assertEquals(true, list.find { it.game.id == wingspan.id }?.isExpanded)
        }
    }

    @Test
    fun changeAllExpendedGameCover_allExpanded_allBecomeFolded() = runTest(testDispatcher) {
        viewModel.state.test {
            viewModel.refresh()
            skipItems(1)

            viewModel.changeAllExpendedGameCover()

            val list = expectMostRecentItem().gameList ?: emptyList()
            list.forEach { assertEquals(false, it.isExpanded) }
        }
    }

    @Test
    fun changeAllExpendedGameCover_allFolded_allBecomeExpanded() = runTest(testDispatcher) {
        viewModel.refresh()
        viewModel.changeAllExpendedGameCover() // First toggle to false

        viewModel.state.test {
            skipItems(1)

            viewModel.changeAllExpendedGameCover() // Toggle back to true

            val list = expectMostRecentItem().gameList ?: emptyList()
            list.forEach { assertEquals(true, it.isExpanded) }
        }
    }

    // -------------------------------------------------------------------------
    // Delete Logic
    // -------------------------------------------------------------------------

    @Test
    fun validateDeleteGame_success_clearsErrorAndLoading() = runTest(testDispatcher) {
        coEvery { gameRepo.deleteGame(any()) } returns RequestResult.Success(true)
        coEvery { gameRepo.getAllGame() } returns RequestResult.Success(listOf(chess, azul))

        viewModel.state.test {
            skipItems(1)

            viewModel.validateDeleteGame(wingspan)

            val finalState = expectMostRecentItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(2, finalState.gameList?.size)
        }
    }

    @Test
    fun validateDeleteGame_error_showsError() = runTest(testDispatcher) {
        coEvery { gameRepo.deleteGame(any()) } returns RequestResult.Error(Exception("Failed"))

        viewModel.events.test {
            viewModel.validateDeleteGame(wingspan)
            assertEquals(
                GameListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
    }

    // -------------------------------------------------------------------------
    // refresh() — error propagation (error is no longer swallowed)
    // -------------------------------------------------------------------------

    @Test
    fun refresh_repositoryError_setsErrorVisible() = runTest(testDispatcher) {
        coEvery { gameRepo.getAllGame() } returns RequestResult.Error(Exception("db error"))

        viewModel.events.test {
            viewModel.refresh()
            assertEquals(
                GameListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
    }
}
