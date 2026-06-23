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

    val game1 = SearchBGGListElements(id = "1", name = "Azul",              yearPublished = 2015)
    val game2 = SearchBGGListElements(id = "2", name = "Wingspan",           yearPublished = 2019)
    val game3 = SearchBGGListElements(id = "3", name = "Catanalida",         yearPublished = 2022)
    val game4 = SearchBGGListElements(id = "4", name = "Catan And Pirates",  yearPublished = null)
    val game5 = SearchBGGListElements(id = "5", name = "Catan",              yearPublished = 2025)
    val game6 = SearchBGGListElements(id = "6", name = "Catan Two",          yearPublished = 2026)
    val game7 = SearchBGGListElements(id = "7", name = null,                 yearPublished = 2026)

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

    @Test
    fun searchGame_setsIsLoadingTrueAndResetsSortBeforeApiCall() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns RequestResult.Success(
            SearchBGGList(boardGames = listOf(game5, game6))
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
    fun searchGame_success_populatesGameListAndGameListSorted() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns RequestResult.Success(
            SearchBGGList(boardGames = listOf(game5, game6))
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
    fun searchGame_success_emptyResults_gameListIsEmpty() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns RequestResult.Success(
            SearchBGGList(boardGames = emptyList())
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
            assertEquals(viewModel.gameList.value, emptyList<SearchBGGListElements>())
            assertEquals(viewModel.gameListSorted.value, emptyList<SearchBGGListElements>())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchGame_error_setsErrorFlagAndClearsLoading() = runTest(testDispatcher) {
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
    fun updateSortedList_nameAscending_sortsByNameAlphabetically() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
            RequestResult.Success(SearchBGGList(boardGames = listOf(game1, game2, game3, game4)))

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
    fun updateSortedList_nameDescending_sortsByNameReverseAlphabetically() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
            RequestResult.Success(SearchBGGList(boardGames = listOf(game1, game2, game3, game4)))

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
    fun updateSortedList_playedAscending_sortsByYearPublishedAscending() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
            RequestResult.Success(SearchBGGList(boardGames = listOf(game1, game2, game3, game4, game5)))

        viewModel.gameListSorted.test {
            skipItems(1)  // skip initial null
            viewModel.searchGame("catan")
            viewModel.update(viewModel.state.value.copy(sortOption = R.string.played_ascending))
            skipItems(1)
            viewModel.updateSortedList()
            assertEquals(listOf(game4, game1, game2, game3, game5), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateSortedList_playedDescending_sortsByYearPublishedDescending() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
            RequestResult.Success(SearchBGGList(boardGames = listOf(game1, game2, game3, game4, game5)))

        viewModel.gameListSorted.test {
            skipItems(1)  // skip initial null
            viewModel.searchGame("catan")
            viewModel.update(viewModel.state.value.copy(sortOption = R.string.played_descending))
            skipItems(1)
            viewModel.updateSortedList()
            assertEquals(listOf(game5, game3, game2, game1, game4), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateSortedList_withSearchTxt_filterAppliedAfterSort() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
            RequestResult.Success(SearchBGGList(boardGames = listOf(game1, game2, game3, game4, game5)))

        viewModel.gameListSorted.test {
            skipItems(1)  // skip initial null
            viewModel.searchGame("catan")
            viewModel.update(viewModel.state.value.copy(sortOption = R.string.name_ascending, searchTxt = "az"))
            skipItems(1)
            viewModel.updateSortedList()
            assertEquals(listOf(game1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateSortedList_elementWithNullName_isExcludedFromResults() = runTest(testDispatcher) {
        coEvery { repo.searchGame("catan") } returns
            RequestResult.Success(SearchBGGList(boardGames = listOf(game2, game7, game1)))

        viewModel.gameListSorted.test {
            skipItems(1)  // skip initial null
            viewModel.searchGame("catan")
            viewModel.update(viewModel.state.value.copy(sortOption = R.string.name_ascending))
            skipItems(1)
            viewModel.updateSortedList()
            assertEquals(listOf(game1, game2), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
