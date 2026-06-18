// Tier 1 — ViewModel state / sorting / filtering logic
//
// Required deps to add before implementing:
//   testImplementation("io.mockk:mockk:1.13.14")
//   testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
//
// Setup pattern:
//   @Before: Dispatchers.setMain(UnconfinedTestDispatcher())
//            gameDbRepository  = mockk<GameDbRepository>()
//            getAllGameUseCase  = mockk<GetAllGameUseCase>()
//            viewModel         = GameListViewModel(gameDbRepository, getAllGameUseCase)
//            Note: init { refresh() } runs immediately — mock getAllGameUseCase.invoke()
//            to return emptyList() as the default stub.
//   @After:  Dispatchers.resetMain()
//
// Tip: refreshGameList() is public and operates purely on the current state,
// so most tests can set state via viewModel.update(state.copy(...)) and then
// call refreshGameList() directly — no coroutines needed for those cases.

package com.goskar.boardgame.ui.gamesList.lists

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
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
class GameListViewModelTest {

    private lateinit var gameRepo: GameDbRepository
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: GameListViewModel

    private val chess = createGame(name = "Chess", games = 8)
    private val azul = createGame(name = "Azul", games = 2)
    private val wingspan = createGame(name = "Wingspan", games = 5)
    private val wingspanExpansion =
        createGame(name = "Wingspan: European Expansion", expansion = true, games = 1)

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

    private val game1 = GameUiState(chess, true)
    private val game2 = GameUiState(azul, true)
    private val game3 = GameUiState(wingspan, true)
    private val game4 = GameUiState(wingspanExpansion, true)


    private fun createGameUiState(game: Game, isExpanded: Boolean = true) =
        GameUiState(game = game, isExpanded = isExpanded)

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        gameRepo = mockk<GameDbRepository>()
        getAllGameUseCase = mockk<GetAllGameUseCase>()

        coEvery { getAllGameUseCase.invoke() } returns emptyList()

        viewModel =
            GameListViewModel(gameDbRepository = gameRepo, getAllGameUseCase = getAllGameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // refreshGameList() — sort options
    // Uses state.sortOption (Int = R.string.*) and state.gameList as input,
    // writes result to state.gameListEdited
    // -------------------------------------------------------------------------

    @Test
    fun refreshGameList_defaultSort_preservesOriginalOrder() = runTest(testDispatcher) {
        // Given: state.gameList has 4 games in original order
        //        state.sortOption = R.string.default_sort
        viewModel.update(
            state = viewModel.state.value.copy(
                gameList = listOf(game1, game2, game3, game4),
                sortOption = R.string.default_sort
            )
        )

        // When:  refreshGameList() is called
        viewModel.refreshGameList()

        // Then:  state.gameListEdited has the same order as state.gameList
        viewModel.state.test {
            val finalItems = awaitItem()
            assertEquals(listOf(game1, game2, game3, game4), finalItems.gameListEdited)
        }
    }

    @Test
    fun refreshGameList_nameAscending_sortsAlphabetically() = runTest(testDispatcher) {
                // Given: state.gameList = [Chess, Azul, Wingspan]
        viewModel.update(
            state = viewModel.state.value.copy(
                gameList = listOf(game1, game2, game3),
                sortOption = R.string.name_ascending
            )
        )

        viewModel.refreshGameList()

        viewModel.state.test {
            val finalItems = awaitItem()

            assertEquals(listOf( game2, game1, game3), finalItems.gameListEdited)

        }

        // When:  refreshGameList() is called
        // Then:  state.gameListEdited = [Azul, Chess, Wingspan]
    }

    @Test
    fun refreshGameList_nameDescending_sortsReverseAlphabetically() {
        // Given: state.gameList = [Azul, Chess, Wingspan]
        //        state.sortOption = R.string.name_descending
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited = [Wingspan, Chess, Azul]
    }

    @Test
    fun refreshGameList_playedAscending_sortsByGamesCountLowToHigh() {
        // Given: games with games counts: Wingspan=5, Azul=2, Chess=8
        //        state.sortOption = R.string.played_ascending
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited order = Azul(2), Wingspan(5), Chess(8)
    }

    @Test
    fun refreshGameList_playedDescending_sortsByGamesCountHighToLow() {
        // Given: games with games counts: Wingspan=5, Azul=2, Chess=8
        //        state.sortOption = R.string.played_descending
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited order = Chess(8), Wingspan(5), Azul(2)
    }

    @Test
    fun refreshGameList_unknownSortOption_treatsAsDefault() {
        // Given: state.sortOption = -1 (hits the else branch)
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited preserves original order (same as default)
    }

    // -------------------------------------------------------------------------
    // refreshGameList() — search filter
    // Applied after sorting: filters by game.name.lowercase().contains(searchTxt)
    // -------------------------------------------------------------------------

    @Test
    fun refreshGameList_searchTxtMatches_onlyMatchingGamesInResult() {
        // Given: games = [Azul, Chess, Wingspan]
        //        state.searchTxt = "az"
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited contains only Azul
    }

    @Test
    fun refreshGameList_searchTxtMatchesNone_resultIsEmpty() {
        // Given: games = [Azul, Chess, Wingspan]
        //        state.searchTxt = "zzz"
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited is empty
    }

    @Test
    fun refreshGameList_searchTxtIsCaseInsensitive() {
        // Given: game.name = "Chess", state.searchTxt = "CHESS"
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited contains Chess (case does not block match)
    }

    @Test
    fun refreshGameList_emptySearchTxt_allGamesPassFilter() {
        // Given: 3 games, state.searchTxt = ""
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited contains all 3 games
    }

    // -------------------------------------------------------------------------
    // refreshGameList() — checkbox visibility filters
    // checkboxBaseGame controls expansion=false items
    // checkboxExpansionGame controls expansion=true items
    // -------------------------------------------------------------------------

    @Test
    fun refreshGameList_bothCheckboxesTrue_allGamesVisible() {
        // Given: 2 base games + 2 expansions
        //        checkboxBaseGame=true, checkboxExpansionGame=true
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited contains all 4 games
    }

    @Test
    fun refreshGameList_onlyBaseCheckbox_onlyBaseGamesVisible() {
        // Given: 2 base games (expansion=false) + 2 expansions (expansion=true)
        //        checkboxBaseGame=true, checkboxExpansionGame=false
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited contains only the 2 base games
    }

    @Test
    fun refreshGameList_onlyExpansionCheckbox_onlyExpansionsVisible() {
        // Given: 2 base games + 2 expansions
        //        checkboxBaseGame=false, checkboxExpansionGame=true
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited contains only the 2 expansions
    }

    @Test
    fun refreshGameList_bothCheckboxesFalse_resultIsEmpty() {
        // Given: 2 base games + 2 expansions
        //        checkboxBaseGame=false, checkboxExpansionGame=false
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited is empty
    }

    // -------------------------------------------------------------------------
    // updateExpandedGameCover(game)
    // Toggles isExpanded for the matching game only, then calls refreshGameList()
    // -------------------------------------------------------------------------

    @Test
    fun updateExpandedGameCover_matchingGame_togglesIsExpanded() {
        // Given: state.gameList has a game with isExpanded=true
        // When:  updateExpandedGameCover(thatGame) is called
        // Then:  that game's isExpanded == false in state.gameList
    }

    @Test
    fun updateExpandedGameCover_onlyTargetGameChanges() {
        // Given: 3 games, all isExpanded=true
        // When:  updateExpandedGameCover(game[1]) is called
        // Then:  game[0].isExpanded == true, game[2].isExpanded == true (unchanged)
        //        game[1].isExpanded == false
    }

    // -------------------------------------------------------------------------
    // changeAllExpendedGameCover()
    // Inverts isExpanded for every game in the list
    // -------------------------------------------------------------------------

    @Test
    fun changeAllExpendedGameCover_allExpanded_allBecomeFolded() {
        // Given: 3 games, all isExpanded=true
        // When:  changeAllExpendedGameCover() is called
        // Then:  all games have isExpanded=false
    }

    @Test
    fun changeAllExpendedGameCover_allFolded_allBecomeExpanded() {
        // Given: 3 games, all isExpanded=false
        // When:  changeAllExpendedGameCover() is called
        // Then:  all games have isExpanded=true
    }

    // -------------------------------------------------------------------------
    // validateDeleteGame(game)
    // Calls gameDbRepository.deleteGame(), then refresh() on success
    // -------------------------------------------------------------------------

    @Test
    fun validateDeleteGame_success_clearsErrorAndLoading() {
        // Given: gameDbRepository.deleteGame() returns RequestResult.Success(Unit)
        //        getAllGameUseCase.invoke() returns emptyList() (for the refresh call)
        // When:  validateDeleteGame(game) is called
        // Then:  state.isLoading == false
        //        state.errorVisible == false
    }

    @Test
    fun validateDeleteGame_error_showsError() {
        // Given: gameDbRepository.deleteGame() returns RequestResult.Error(Throwable())
        // When:  validateDeleteGame(game) is called
        // Then:  state.errorVisible == true
        //        state.isLoading == false
    }
}
