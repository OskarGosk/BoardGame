package com.goskar.boardgame.ui.playerList

import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.ui.components.other.AppSnackBarType
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
class PlayerListViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var repo: PlayerDbRepository
    private lateinit var viewModel: PlayerListViewModel

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
        selected = true
    )
    val player1edited = player1.copy(name = "Bob")

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repo = mockk<PlayerDbRepository>()
        viewModel = PlayerListViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getAllPlayer_setsIsLoadingTrueBeforeRepositoryCallAndFalseAfter() =
        runTest(testDispatcher) {
            coEvery { repo.getAllPlayer() } returns RequestResult.Success(emptyList())

            viewModel.state.test {
                skipItems(1)
                viewModel.getAllPlayer()
                assertEquals(true, awaitItem().isLoading)
                assertEquals(false, awaitItem().isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun getAllPlayer_success_setsPlayerListAndClearsLoading() = runTest(testDispatcher) {
        coEvery { repo.getAllPlayer() } returns RequestResult.Success(listOf(player1, player2))

        viewModel.state.test {
            viewModel.getAllPlayer()
            skipItems(2)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(listOf(player1, player2), item.playerList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllPlayer_success_emptyList_setsEmptyPlayerList() = runTest(testDispatcher) {
        coEvery { repo.getAllPlayer() } returns RequestResult.Success(emptyList())

        viewModel.state.test {
            viewModel.getAllPlayer()
            skipItems(2)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(emptyList<Player>(), item.playerList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllPlayer_error_showsErrorAndClearsLoading() = runTest(testDispatcher) {
        coEvery { repo.getAllPlayer() } returns RequestResult.Error(Throwable("db error"))

        viewModel.events.test {
            viewModel.getAllPlayer()
            assertEquals(
                PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun validateDeletePlayer_success_emitsSuccessAndRefreshesList() = runTest(testDispatcher) {
        coEvery { repo.deletePlayer(player1) } returns RequestResult.Success(true)
        coEvery { repo.getAllPlayer() } returns RequestResult.Success(listOf(player2))

        viewModel.events.test {
            viewModel.validateDeletePlayer(player1)
            assertEquals(
                PlayerListEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS),
                awaitItem()
            )
        }
        assertEquals(listOf(player2), viewModel.state.value.playerList)
    }

    @Test
    fun validateDeletePlayer_error_showsError() = runTest(testDispatcher) {
        coEvery { repo.deletePlayer(player1) } returns RequestResult.Error(Throwable("db error"))

        viewModel.events.test {
            viewModel.validateDeletePlayer(player1)
            assertEquals(
                PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
    }

    @Test
    fun validateAddEditPlayer_nullPlayer_showsError() = runTest(testDispatcher) {
        viewModel.events.test {
            viewModel.validateAddEditPLayer(newPlayer = true)
            assertEquals(
                PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
    }

    @Test
    fun validateAddEditPlayer_newPlayer_success_clearsPlayerAndRefreshes() =
        runTest(testDispatcher) {
            coEvery { repo.getAllPlayer() } returns
                    RequestResult.Success(listOf(player1)) andThen
                    RequestResult.Success(listOf(player1, player2))
            coEvery { repo.insertPlayer(player2) } returns RequestResult.Success(true)

            viewModel.state.test {
                viewModel.getAllPlayer()
                skipItems(2)
                val item = awaitItem()
                assertEquals(listOf(player1), item.playerList)

                viewModel.updatePlayer(player2)
                viewModel.validateAddEditPLayer(newPlayer = true)

                val loadingItem = awaitItem()
                assertEquals(player2, loadingItem.player)

                val nextItem = awaitItem()
                assertEquals(null, nextItem.player)
                assertEquals(true, nextItem.isLoading)

                val finalItem = awaitItem()
                assertEquals(null, finalItem.player)
                assertEquals(listOf(player1, player2), finalItem.playerList)
                assertEquals(false, finalItem.isLoading)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun validateAddEditPlayer_newPlayer_error_showsError() = runTest(testDispatcher) {
        coEvery { repo.insertPlayer(player2) } returns RequestResult.Error(Throwable("db error"))

        viewModel.updatePlayer(player2)
        viewModel.events.test {
            viewModel.validateAddEditPLayer(newPlayer = true)
            assertEquals(
                PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
        assertEquals(player2, viewModel.state.value.player)
    }

    @Test
    fun validateAddEditPlayer_editExistingPlayer_callsEditNotInsert() = runTest(testDispatcher) {
        coEvery { repo.getAllPlayer() } returns
                RequestResult.Success(listOf(player1, player2)) andThen
                RequestResult.Success(listOf(player1edited, player2))
        coEvery { repo.editPlayer(player1edited) } returns RequestResult.Success(true)
        coEvery { repo.insertPlayer(player1edited) } returns RequestResult.Error(Throwable("should not be called"))

        viewModel.state.test {
            skipItems(1)
            viewModel.getAllPlayer()
            assertEquals(true, awaitItem().isLoading)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(listOf(player1, player2), item.playerList)

            viewModel.updatePlayer(player1edited)
            viewModel.validateAddEditPLayer(newPlayer = false)

            skipItems(1)
            val item2 = awaitItem()
            assertEquals(null, item2.player)
            assertEquals(true, item2.isLoading)

            val finalItem = awaitItem()
            assertEquals(false, finalItem.isLoading)
            assertEquals(listOf(player1edited, player2), finalItem.playerList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun validateAddEditPlayer_editExistingPlayer_error_showsError() = runTest(testDispatcher) {
        coEvery { repo.editPlayer(player1edited) } returns RequestResult.Error(Throwable("db error"))

        viewModel.updatePlayer(player1edited)
        viewModel.events.test {
            viewModel.validateAddEditPLayer(false)
            assertEquals(
                PlayerListEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
        assertEquals(player1edited, viewModel.state.value.player)
    }
}
