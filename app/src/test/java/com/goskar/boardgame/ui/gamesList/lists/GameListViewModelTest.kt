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

import org.junit.After
import org.junit.Before
import org.junit.Test

class GameListViewModelTest {

    @Before
    fun setUp() {
        // Dispatchers.setMain(UnconfinedTestDispatcher())
        // gameDbRepository = mockk<GameDbRepository>()
        // getAllGameUseCase = mockk<GetAllGameUseCase>()
        // coEvery { getAllGameUseCase.invoke() } returns emptyList()
        // viewModel = GameListViewModel(gameDbRepository, getAllGameUseCase)
    }

    @After
    fun tearDown() {
        // Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // refreshGameList() — sort options
    // Uses state.sortOption (Int = R.string.*) and state.gameList as input,
    // writes result to state.gameListEdited
    // -------------------------------------------------------------------------

    @Test
    fun refreshGameList_defaultSort_preservesOriginalOrder() {
        // Given: state.gameList has 3 games in arbitrary order
        //        state.sortOption = R.string.default_sort
        // When:  refreshGameList() is called
        // Then:  state.gameListEdited has the same order as state.gameList
    }

    @Test
    fun refreshGameList_nameAscending_sortsAlphabetically() {
        // Given: state.gameList = [Chess, Azul, Wingspan] (unsorted)
        //        state.sortOption = R.string.name_ascending
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
