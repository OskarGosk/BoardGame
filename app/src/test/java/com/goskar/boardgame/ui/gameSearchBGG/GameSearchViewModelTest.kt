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

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.SearchBGGList
import com.goskar.boardgame.data.models.SearchBGGListElements
import com.goskar.boardgame.data.repository.bbg.BoardGameApiRepository
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
class GameSearchViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var repo: BoardGameApiRepository
    private lateinit var viewModel: GameSearchViewModel

    val game1 = SearchBGGListElements(id = "1", name = "Azul", yearPublished = 2015)
    val game2 = SearchBGGListElements(id = "2", name = "Wingspan", yearPublished = 2019)
    val game3 = SearchBGGListElements(id = "3", name = "Catanalida", yearPublished = 2022)
    val game4 = SearchBGGListElements(id = "4", name = "Catan And Pirates", yearPublished = null)
    val game5 = SearchBGGListElements(id = "5", name = "Catan", yearPublished = 2025)
    val game6 = SearchBGGListElements(id = "6", name = "Catan Two", yearPublished = 2026)
    val game7 = SearchBGGListElements(id = "6", name = null, yearPublished = 2026)



    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repo = mockk<BoardGameApiRepository>()
        viewModel = GameSearchViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // searchGame(name)
    // -------------------------------------------------------------------------

    @Test
    fun searchGame_setsIsLoadingTrueAndResetsSortBeforeApiCall() = runTest(testDispatcher) {
        // Given: any API stub (stub it to not return immediately if capturing state mid-flight)
        // When:  searchGame("catan") is called
        // Then:  at the moment the API call starts:
        //        state.isLoading   == true
        //        state.sortOption  == R.string.default_sort  (reset regardless of prior value)

        coEvery { repo.searchGame("catan") } returns RequestResult.Success(
            SearchBGGList(
                boardGames = listOf(
                    game5,
                    game6
                )
            )
        )

        viewModel.state.test {
            viewModel.searchGame("catan")

            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(R.string.default_sort, loadingState.sortOption)

            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchGame_success_populatesGameListAndGameListSorted() = runTest {
        // Given: repository.searchGame("catan") returns
        //          RequestResult.Success(SearchList(boardGames = listOf(elem1, elem2)))
        // When:  searchGame("catan") is called
        // Then:  gameList.value      == listOf(elem1, elem2)
        //        gameListSorted.value == listOf(elem1, elem2)   (default sort = original order)
        //        state.isLoading     == false
        //        state.error         == false

        coEvery { repo.searchGame("catan") } returns RequestResult.Success(
            SearchBGGList(
                boardGames = listOf(
                    game5,
                    game6
                )
            )
        )

        viewModel.state.test {
            viewModel.searchGame("catan")

            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(R.string.default_sort, loadingState.sortOption)

            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(false, finalState.error)
            assertEquals(viewModel.gameList.value, listOf(game5, game6))
            assertEquals(viewModel.gameListSorted.value, listOf(game5, game6))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchGame_success_emptyResults_gameListIsEmpty() = runTest {
        // Given: repository.searchGame("xyz") returns Success(SearchList(boardGames = emptyList()))
        // When:  searchGame("xyz") is called
        // Then:  gameList.value is empty
        //        gameListSorted.value is empty
        //        state.isLoading == false

        coEvery { repo.searchGame("catan") } returns RequestResult.Success(SearchBGGList(boardGames = emptyList()))

        viewModel.state.test {
            viewModel.searchGame("catan")

            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(R.string.default_sort, loadingState.sortOption)

            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(false, finalState.error)
            assertEquals(viewModel.gameList.value, emptyList<SearchBGGListElements>())
            assertEquals(viewModel.gameListSorted.value, emptyList<SearchBGGListElements>())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchGame_error_setsErrorFlagAndClearsLoading() = runTest {
        // Given: repository.searchGame("catan") returns RequestResult.Error(Throwable("network"))
        // When:  searchGame("catan") is called
        // Then:  state.error     == true
        //        state.isLoading == false
        //        gameList.value  is unchanged (null, since it was never set)

        coEvery { repo.searchGame("catan") } returns RequestResult.Error(Throwable("network"))

        viewModel.state.test {
            viewModel.searchGame("catan")

            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(R.string.default_sort, loadingState.sortOption)

            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(true, finalState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // updateSortedList() — sort options
    // Reads from _gameList and _state.sortOption, writes to _gameListSorted
    // Call via viewModel.update(state.copy(sortOption = ...)) then updateSortedList()
    // Pre-populate _gameList by doing a successful searchGame() call first.
    // -------------------------------------------------------------------------

    @Test
    fun updateSortedList_defaultSort_preservesApiOrder() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
                RequestResult.Success(SearchBGGList(boardGames = listOf(game1, game2, game3)))

        viewModel.gameListSorted.test {
            skipItems(1)  // skip initial null
            viewModel.searchGame("catan")
            viewModel.updateSortedList()
            assertEquals(listOf(game1, game2, game3), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateSortedList_nameAscending_sortsByNameAlphabetically()
    // Given: gameList = [Wingspan, Azul, Chess]
    //        state.sortOption = R.string.name_ascending
    // When:  updateSortedList() is called
    // Then:  gameListSorted = [Azul, Chess, Wingspan]

            = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
                RequestResult.Success(
                    SearchBGGList(
                        boardGames = listOf(
                            game1,
                            game2,
                            game3,
                            game4
                        )
                    )
                )

        viewModel.gameListSorted.test {
            skipItems(1)  // skip initial null
            viewModel.searchGame("catan")
            viewModel.update(viewModel.state.value.copy(sortOption = R.string.name_ascending))
            skipItems(1)
            viewModel.updateSortedList()
            assertEquals(listOf(game1, game4, game3, game2), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateSortedList_nameDescending_sortsByNameReverseAlphabetically()
    // Given: gameList = [Azul, Chess, Wingspan]
    //        state.sortOption = R.string.name_descending
    // When:  updateSortedList() is called
    // Then:  gameListSorted = [Wingspan, Chess, Azul]

            = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
                RequestResult.Success(
                    SearchBGGList(
                        boardGames = listOf(
                            game1,
                            game2,
                            game3,
                            game4
                        )
                    )
                )

        viewModel.gameListSorted.test {
            skipItems(1)  // skip initial null
            viewModel.searchGame("catan")
            viewModel.update(viewModel.state.value.copy(sortOption = R.string.name_descending))
            skipItems(1)
            viewModel.updateSortedList()
            assertEquals(listOf(game2, game3, game4, game1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateSortedList_playedAscending_sortsByYearPublishedAscending()
        // Given: gameList has elements with yearPublished: 2019, 2015, 2022
        //        state.sortOption = R.string.played_ascending
        // When:  updateSortedList() is called
        // Then:  gameListSorted order = 2015, 2019, 2022

        = runTest(testDispatcher) {
            coEvery { repo.searchGame("catan") } returns
                    RequestResult.Success(
                        SearchBGGList(
                            boardGames = listOf(
                                game1,
                                game2,
                                game3,
                                game4,
                                game5
                            )
                        )
                    )

            viewModel.gameListSorted.test {
                skipItems(1)  // skip initial null
                viewModel.searchGame("catan")
                viewModel.update(viewModel.state.value.copy(sortOption = R.string.played_ascending))
                skipItems(1)
                viewModel.updateSortedList()
                assertEquals(
                    listOf(game4, game1, game2, game3, game5), awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun updateSortedList_playedDescending_sortsByYearPublishedDescending()
            // Given: gameList has elements with yearPublished: 2019, 2015, 2022
            //        state.sortOption = R.string.played_descending
            // When:  updateSortedList() is called
            // Then:  gameListSorted order = 2022, 2019, 2015

                = runTest(testDispatcher) {
            coEvery { repo.searchGame("catan") } returns
                    RequestResult.Success(
                        SearchBGGList(
                            boardGames = listOf(
                                game1,
                                game2,
                                game3,
                                game4,
                                game5
                            )
                        )
                    )

            viewModel.gameListSorted.test {
                skipItems(1)  // skip initial null
                viewModel.searchGame("catan")
                viewModel.update(viewModel.state.value.copy(sortOption = R.string.played_descending))
                skipItems(1)
                viewModel.updateSortedList()
                assertEquals(
                    listOf(game5, game3, game2, game1, game4), awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun updateSortedList_withSearchTxt_filterAppliedAfterSort()
            // Given: gameList = [Azul, Chess, Wingspan]
            //        state.sortOption = R.string.name_ascending
            //        state.searchTxt  = "az"
            // When:  updateSortedList() is called
            // Then:  gameListSorted = [Azul]  (sorted, then filtered)

            = runTest(testDispatcher) {
                coEvery { repo.searchGame("catan") } returns
                        RequestResult.Success(
                            SearchBGGList(
                                boardGames = listOf(
                                    game1,
                                    game2,
                                    game3,
                                    game4,
                                    game5
                                )
                            )
                        )

                viewModel.gameListSorted.test {
                    skipItems(1)  // skip initial null
                    viewModel.searchGame("catan")
                    viewModel.update(viewModel.state.value.copy(sortOption = R.string.name_ascending, searchTxt = "az"))
                    skipItems(1)
                    viewModel.updateSortedList()
                    assertEquals(
                        listOf(game1), awaitItem()
                    )
                    cancelAndIgnoreRemainingEvents()
                }
        }

        @Test
        fun updateSortedList_elementWithNullName_isExcludedFromResults()
            // Given: gameList = [element(name=null), element(name="Chess")]
            //        state.searchTxt = ""
            // When:  updateSortedList() is called
            // Then:  gameListSorted contains only Chess (null-name element filtered out)
            // Note:  filter uses it.name?.lowercase()?.contains(...) == true,
            //        so null name produces false and is excluded
            = runTest(testDispatcher) {
                coEvery { repo.searchGame("catan") } returns
                        RequestResult.Success(
                            SearchBGGList(
                                boardGames = listOf(
                                    game2,
                                    game7,
                                    game1
                                )
                            )
                        )

                viewModel.gameListSorted.test {
                    skipItems(1)  // skip initial null
                    viewModel.searchGame("catan")
                    viewModel.update(viewModel.state.value.copy(sortOption = R.string.name_ascending))
                    skipItems(1)
                    viewModel.updateSortedList()
                    assertEquals(
                        listOf(game1,game2), awaitItem()
                    )
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }
