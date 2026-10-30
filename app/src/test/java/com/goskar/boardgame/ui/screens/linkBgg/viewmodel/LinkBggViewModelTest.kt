package com.goskar.boardgame.ui.screens.linkBgg.viewmodel

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.SearchBGGList
import com.goskar.boardgame.data.models.SearchBGGListElements
import com.goskar.boardgame.data.repository.bgg.BoardGameApiRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.ui.screens.addGame.viewmodel.PopularMatch
import io.mockk.coEvery
import io.mockk.coVerify
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
class LinkBggViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var api: BoardGameApiRepository
    private lateinit var viewModel: LinkBggViewModel

    private val virusResults = SearchBGGList(
        listOf(
            SearchBGGListElements(id = "180263", name = "Virus!", yearPublished = 2015),
            SearchBGGListElements(id = "7", name = "Virus", yearPublished = null),
        )
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        viewModel = LinkBggViewModel(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun start_prefillsQueryWithGameNameAndSearches() = runTest(testDispatcher) {
        coEvery { api.searchGame("Wirus!") } returns RequestResult.Success(virusResults)

        viewModel.start("Wirus!")

        val state = viewModel.state.value
        assertEquals("Wirus!", state.gameName)
        assertEquals("Wirus!", state.query)
        assertEquals(true, state.searched)
        assertEquals(
            listOf(
                PopularMatch(id = "180263", name = "Virus!", category = "BGG", year = "2015"),
                PopularMatch(id = "7", name = "Virus", category = "BGG", year = "—"),
            ),
            state.matches
        )
    }

    @Test
    fun start_again_keepsResultsWithoutSearchingTwice() = runTest(testDispatcher) {
        coEvery { api.searchGame(any()) } returns RequestResult.Success(virusResults)
        viewModel.start("Wirus!")
        viewModel.updateQuery("Virus")
        viewModel.search()

        viewModel.start("Wirus!")

        assertEquals("Virus", viewModel.state.value.query)
        coVerify(exactly = 1) { api.searchGame("Wirus!") }
    }

    @Test
    fun search_noMatches_marksSearchedWithEmptyList() = runTest(testDispatcher) {
        coEvery { api.searchGame(any()) } returns RequestResult.Success(SearchBGGList(null))

        viewModel.start("Leśny Bal")

        assertEquals(true, viewModel.state.value.searched)
        assertEquals(emptyList<PopularMatch>(), viewModel.state.value.matches)
    }

    @Test
    fun search_error_showsMessageAndStopsLoading() = runTest(testDispatcher) {
        coEvery { api.searchGame(any()) } returns RequestResult.Error(Throwable("offline"))

        viewModel.events.test {
            viewModel.start("Wirus!")
            assertEquals(LinkBggEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR), awaitItem())
        }
        assertEquals(false, viewModel.state.value.isSearching)
        assertEquals(false, viewModel.state.value.searched)
    }

    @Test
    fun search_tooShortQuery_clearsResultsWithoutCallingBgg() = runTest(testDispatcher) {
        coEvery { api.searchGame(any()) } returns RequestResult.Success(virusResults)
        viewModel.start("Wirus!")

        viewModel.updateQuery(" W ")
        viewModel.search()

        assertEquals(emptyList<PopularMatch>(), viewModel.state.value.matches)
        coVerify(exactly = 1) { api.searchGame(any()) }
    }
}
