// Tier 2 — API search, two StateFlows, sorting/filtering of BGG results
//
// Required deps to add before implementing:
//   testImplementation("io.mockk:mockk:1.13.14")
//   testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
//   testImplementation("app.cash.turbine:turbine:1.2.0")
//
// Setup pattern:
//   @Before: Dispatchers.setMain(UnconfinedTestDispatcher())
//            repository = mockk<BoardGameApiRepository>()
//            viewModel  = GameSearchViewModel(repository)
//            Note: no init work — construction is safe without pre-stubbing.
//   @After:  Dispatchers.resetMain()
//
// Key state model:
//   _state         → GameSearchState (isLoading, sortOption, searchTxt, error)
//   _gameList      → the raw search results from the API
//   _gameListSorted → derived: sorted + filtered copy of _gameList
//
// updateSortedList() is public and operates purely on current _gameList and _state,
// so tests for sorting can call it directly after setting up state.

package com.goskar.boardgame.ui.gameSearchBGG

import org.junit.After
import org.junit.Before
import org.junit.Test

class GameSearchViewModelTest {

    @Before
    fun setUp() {
        // Dispatchers.setMain(UnconfinedTestDispatcher())
        // repository = mockk<BoardGameApiRepository>()
        // viewModel  = GameSearchViewModel(repository)
    }

    @After
    fun tearDown() {
        // Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // searchGame(name)
    // -------------------------------------------------------------------------

    @Test
    fun searchGame_setsIsLoadingTrueAndResetsSortBeforeApiCall() {
        // Given: any API stub (stub it to not return immediately if capturing state mid-flight)
        // When:  searchGame("catan") is called
        // Then:  at the moment the API call starts:
        //        state.isLoading   == true
        //        state.sortOption  == R.string.default_sort  (reset regardless of prior value)
    }

    @Test
    fun searchGame_success_populatesGameListAndGameListSorted() {
        // Given: repository.searchGame("catan") returns
        //          RequestResult.Success(SearchList(boardGames = listOf(elem1, elem2)))
        // When:  searchGame("catan") is called
        // Then:  gameList.value      == listOf(elem1, elem2)
        //        gameListSorted.value == listOf(elem1, elem2)   (default sort = original order)
        //        state.isLoading     == false
        //        state.error         == false
    }

    @Test
    fun searchGame_success_emptyResults_gameListIsEmpty() {
        // Given: repository.searchGame("xyz") returns Success(SearchList(boardGames = emptyList()))
        // When:  searchGame("xyz") is called
        // Then:  gameList.value is empty
        //        gameListSorted.value is empty
        //        state.isLoading == false
    }

    @Test
    fun searchGame_error_setsErrorFlagAndClearsLoading() {
        // Given: repository.searchGame("catan") returns RequestResult.Error(Throwable("network"))
        // When:  searchGame("catan") is called
        // Then:  state.error     == true
        //        state.isLoading == false
        //        gameList.value  is unchanged (null, since it was never set)
    }

    // -------------------------------------------------------------------------
    // updateSortedList() — sort options
    // Reads from _gameList and _state.sortOption, writes to _gameListSorted
    // Call via viewModel.update(state.copy(sortOption = ...)) then updateSortedList()
    // Pre-populate _gameList by doing a successful searchGame() call first.
    // -------------------------------------------------------------------------

    @Test
    fun updateSortedList_defaultSort_preservesApiOrder() {
        // Given: a prior searchGame() left gameList = [B, A, C] (arbitrary order)
        //        state.sortOption = R.string.default_sort
        // When:  updateSortedList() is called
        // Then:  gameListSorted == [B, A, C]  (same order as gameList)
    }

    @Test
    fun updateSortedList_nameAscending_sortsByNameAlphabetically() {
        // Given: gameList = [Wingspan, Azul, Chess]
        //        state.sortOption = R.string.name_ascending
        // When:  updateSortedList() is called
        // Then:  gameListSorted = [Azul, Chess, Wingspan]
    }

    @Test
    fun updateSortedList_nameDescending_sortsByNameReverseAlphabetically() {
        // Given: gameList = [Azul, Chess, Wingspan]
        //        state.sortOption = R.string.name_descending
        // When:  updateSortedList() is called
        // Then:  gameListSorted = [Wingspan, Chess, Azul]
    }

    @Test
    fun updateSortedList_playedAscending_sortsByYearPublishedAscending() {
        // Given: gameList has elements with yearPublished: 2019, 2015, 2022
        //        state.sortOption = R.string.played_ascending
        // When:  updateSortedList() is called
        // Then:  gameListSorted order = 2015, 2019, 2022
    }

    @Test
    fun updateSortedList_playedDescending_sortsByYearPublishedDescending() {
        // Given: gameList has elements with yearPublished: 2019, 2015, 2022
        //        state.sortOption = R.string.played_descending
        // When:  updateSortedList() is called
        // Then:  gameListSorted order = 2022, 2019, 2015
    }

    @Test
    fun updateSortedList_withSearchTxt_filterAppliedAfterSort() {
        // Given: gameList = [Azul, Chess, Wingspan]
        //        state.sortOption = R.string.name_ascending
        //        state.searchTxt  = "az"
        // When:  updateSortedList() is called
        // Then:  gameListSorted = [Azul]  (sorted, then filtered)
    }

    @Test
    fun updateSortedList_elementWithNullName_isExcludedFromResults() {
        // Given: gameList = [element(name=null), element(name="Chess")]
        //        state.searchTxt = ""
        // When:  updateSortedList() is called
        // Then:  gameListSorted contains only Chess (null-name element filtered out)
        // Note:  filter uses it.name?.lowercase()?.contains(...) == true,
        //        so null name produces false and is excluded
    }
}
