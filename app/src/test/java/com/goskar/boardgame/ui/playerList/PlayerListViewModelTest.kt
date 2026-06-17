// Tier 1 — ViewModel state, loading transitions, CRUD success/error paths
//
// Required deps to add before implementing:
//   testImplementation("io.mockk:mockk:1.13.14")
//   testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
//   testImplementation("app.cash.turbine:turbine:1.2.0")  // for Flow emission assertions
//
// Setup pattern:
//   @Before: Dispatchers.setMain(UnconfinedTestDispatcher())
//            repository = mockk<PlayerDbRepository>()
//            viewModel  = PlayerListViewModel(repository)
//            Note: PlayerListViewModel has no init block that launches work,
//            so construction is safe without any pre-stubbing.
//   @After:  Dispatchers.resetMain()
//
// For Flow assertions use Turbine:
//   viewModel.state.test {
//       val item = awaitItem()
//       assertEquals(expected, item.someField)
//   }
//
// Alternatively, with UnconfinedTestDispatcher the coroutine runs eagerly,
// so viewModel.state.value is often readable directly after calling the function.

package com.goskar.boardgame.ui.playerList

import app.cash.turbine.test
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    val testDispatcher = UnconfinedTestDispatcher()
    val repo = mockk<PlayerDbRepository>()
    val viewModel = PlayerListViewModel(repo)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // getAllPlayer()
    // Emits isLoading=true, calls repository, then sets playerList or errorVisible
    // -------------------------------------------------------------------------

    val player1 = Player(
        name = "Alice", id = "id-1",
        games = 2,
        winRatio = 1,
        description = "Test",
        selected = false
    )
    val player2 = Player(
        name = "Bob", id = "id-2",
        games = 3,
        winRatio = 2,
        description = "Test",
        selected = true
    )
    val player1edited = player1.copy(name = "Bob")


    @Test
    fun getAllPlayer_setsIsLoadingTrueBeforeRepositoryCallAndFalseAfter() = runTest {
        // Given: viewModel.getAllPlayer() is stubbed but will not return immediately
        //        (use a coEvery { } that suspends or just capture state mid-flight)
        // When:  getAllPlayer() is called
        // Then:  the first state emission has isLoading == true

        coEvery {
            repo.getAllPlayer()
        } returns RequestResult.Success(emptyList())

        viewModel.state.test {
            skipItems(1)
            viewModel.getAllPlayer()
            assertEquals(true, awaitItem().isLoading)
            val result = awaitItem()
            assertEquals(false, result.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllPlayer_success_setsPlayerListAndClearsLoadingAndError() = runTest {
        // Given: repository.getAllPlayer() returns RequestResult.Success(listOf(player1, player2))
        // When:  getAllPlayer() is called
        // Then:  state.playerList == listOf(player1, player2)
        //        state.isLoading  == false
        //        state.errorVisible == false

        coEvery {
            repo.getAllPlayer()
        } returns RequestResult.Success(listOf(player1, player2))

        viewModel.state.test {
            viewModel.getAllPlayer()
            skipItems(2)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(false, item.errorVisible)
            assertEquals(listOf(player1, player2), item.playerList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllPlayer_success_emptyList_setsEmptyPlayerList() = runTest {
        // Given: repository.getAllPlayer() returns RequestResult.Success(emptyList())
        // When:  getAllPlayer() is called
        // Then:  state.playerList == emptyList()
        //        state.isLoading  == false

        coEvery {
            repo.getAllPlayer()
        } returns RequestResult.Success(emptyList())

        viewModel.state.test {
            viewModel.getAllPlayer()
            skipItems(2)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(false, item.errorVisible)
            assertEquals(emptyList<Player>(), item.playerList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllPlayer_error_showsErrorAndClearsLoading() = runTest {
        // Given: repository.getAllPlayer() returns RequestResult.Error(Throwable("db error"))
        // When:  getAllPlayer() is called
        // Then:  state.errorVisible == true
        //        state.isLoading    == false
        //        state.playerList   is unchanged from before the call

        coEvery {
            repo.getAllPlayer()
        } returns RequestResult.Error(Throwable("db error"))

        viewModel.state.test {
            viewModel.getAllPlayer()
            skipItems(2)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(true, item.errorVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // validateDeletePlayer(player)
    // Calls repository.deletePlayer(), on success sets successDeletePlayer=true
    // and triggers getAllPlayer() to refresh the list
    // -------------------------------------------------------------------------

    @Test
    fun validateDeletePlayer_success_setsSuccessFlagAndRefreshesList() = runTest {
        // Given: repository.deletePlayer() returns RequestResult.Success(Unit)
        //        repository.getAllPlayer() returns RequestResult.Success(updatedList)
        // When:  validateDeletePlayer(player) is called
        // Then:  state.successDeletePlayer == true
        //        state.errorVisible        == false
        //        repository.getAllPlayer() was called (verify with mockk: verify { })
        coEvery { repo.getAllPlayer() } returns
                RequestResult.Success(listOf(player1, player2)) andThen
                RequestResult.Success(listOf(player2))

        coEvery { repo.deletePlayer(player1) } returns RequestResult.Success(true)


        viewModel.state.test {
            viewModel.getAllPlayer()
            skipItems(2)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(false, item.errorVisible)
            assertEquals(listOf(player1, player2), item.playerList)

            viewModel.validateDeletePlayer(player1)
            skipItems(1)
            val item1 = awaitItem()
            assertEquals(true, item1.successDeletePlayer)
            assertEquals(false, item1.errorVisible)
            viewModel.getAllPlayer()
            skipItems(1)
            val item2 = awaitItem()
            assertEquals(false, item2.isLoading)
            assertEquals(false, item2.errorVisible)
            assertEquals(listOf(player2), item2.playerList)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun validateDeletePlayer_error_showsError() = runTest {
        // Given: repository.deletePlayer() returns RequestResult.Error(Throwable())
        // When:  validateDeletePlayer(player) is called
        // Then:  state.errorVisible == true
        //        state.successDeletePlayer is unchanged (false)
        //        repository.getAllPlayer() is NOT called after the error

        coEvery { repo.deletePlayer(player1) } returns RequestResult.Error(Throwable("db error"))

        viewModel.state.test {
            viewModel.validateDeletePlayer(player1)
            skipItems(1)
            val item = awaitItem()
            assertEquals(true, item.errorVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // validateAddEditPLayer(newPlayer: Boolean)
    // When state.player == null → sets errorVisible=true (null branch)
    // When newPlayer=true  → calls insertPlayer()
    // When newPlayer=false → calls editPlayer()
    // On success: clears state.player and calls getAllPlayer()
    // On error:   sets errorVisible=true
    // -------------------------------------------------------------------------

    @Test
    fun validateAddEditPlayer_nullPlayer_setsErrorVisible() {
        // Given: state.player == null (default state)
        // When:  validateAddEditPLayer(newPlayer = true) is called
        // Then:  state.errorVisible == true
        //        neither insertPlayer() nor editPlayer() is called

        viewModel.update(state = viewModel.state.value.copy(player = null))
        viewModel.validateAddEditPLayer(newPlayer = true)
        assertEquals(true, viewModel.state.value.errorVisible)
    }

    @Test
    fun validateAddEditPlayer_newPlayer_success_clearsPlayerAndRefreshes() = runTest {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.insertPlayer(aPlayer) returns RequestResult.Success(Unit)
        //        repository.getAllPlayer() returns RequestResult.Success(listOf(aPlayer))
        // When:  validateAddEditPLayer(newPlayer = true) is called
        // Then:  state.player == null
        //        repository.insertPlayer() was called (not editPlayer())
        //        repository.getAllPlayer() was called

        coEvery { repo.getAllPlayer() } returns
                RequestResult.Success(listOf(player1)) andThen
                RequestResult.Success(listOf(player1, player2))

        coEvery {
            repo.insertPlayer(player2)
        } returns RequestResult.Success(true)

        viewModel.state.test {
            viewModel.getAllPlayer()
            skipItems(2)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(false, item.errorVisible)
            assertEquals(listOf(player1), item.playerList)

            viewModel.update(state = viewModel.state.value.copy(player = player2))
            viewModel.validateAddEditPLayer(newPlayer = true)
            skipItems(1)
            val item2 = awaitItem()
            assertEquals(null, item2.player)
            viewModel.getAllPlayer()
            skipItems(1)
            val item3 = awaitItem()
            assertEquals(listOf(player1,player2), item3.playerList)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun validateAddEditPlayer_newPlayer_error_showsError() = runTest {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.insertPlayer(aPlayer) returns RequestResult.Error(Throwable())
        // When:  validateAddEditPLayer(newPlayer = true) is called
        // Then:  state.errorVisible == true
        //        state.player is unchanged (not cleared)

        coEvery {
            repo.insertPlayer(player2)
        } returns RequestResult.Error(Throwable("db error"))

        viewModel.state.test {
            viewModel.update(state = viewModel.state.value.copy(player = player2))
            viewModel.validateAddEditPLayer(newPlayer = true)
            skipItems(2)
            val item = awaitItem()
            assertEquals(true, item.errorVisible)
            assertEquals(player2, item.player)
        }
    }

    @Test
    fun validateAddEditPlayer_editExistingPlayer_callsEditNotInsert() = runTest {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.editPlayer(aPlayer) returns RequestResult.Success(Unit)
        //        repository.getAllPlayer() returns RequestResult.Success(listOf(aPlayer))
        // When:  validateAddEditPLayer(newPlayer = false) is called
        // Then:  repository.editPlayer() was called (not insertPlayer())
        //        state.player == null


        coEvery { repo.getAllPlayer() } returns
                RequestResult.Success(listOf(player1, player2)) andThen
                RequestResult.Success(listOf(player1edited, player2))

        coEvery { repo.editPlayer(player1edited) } returns RequestResult.Success(true)

        coEvery { repo.insertPlayer(player1edited) } returns RequestResult.Error(Throwable("db error"))

        viewModel.state.test {
            skipItems(1)
            viewModel.getAllPlayer()
            assertEquals(true, awaitItem().isLoading)
            val item = awaitItem()
            assertEquals(false, item.isLoading)
            assertEquals(false, item.errorVisible)
            assertEquals(listOf(player1, player2), item.playerList)

            viewModel.update(state = viewModel.state.value.copy(player = player1edited))
            viewModel.validateAddEditPLayer(newPlayer = false)
            skipItems(1)
            val item2 = awaitItem()
            assertEquals(null, item2.player)
            assertEquals(false, item2.errorVisible)
            viewModel.getAllPlayer()
            skipItems(2)
            val item3 = awaitItem()
            assertEquals(false, item3.isLoading)
            assertEquals(false, item3.errorVisible)
            assertEquals(listOf(player1edited, player2), item3.playerList)

        }

    }

    @Test
    fun validateAddEditPlayer_editExistingPlayer_error_showsError() = runTest {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.editPlayer(aPlayer) returns RequestResult.Error(Throwable())
        // When:  validateAddEditPLayer(newPlayer = false) is called
        // Then:  state.errorVisible == true

        coEvery { repo.editPlayer(player1edited) } returns RequestResult.Error(Throwable("db error"))

        viewModel.state.test {
            viewModel.update(state = viewModel.state.value.copy(player = player1edited))
            skipItems(1)

            viewModel.validateAddEditPLayer(false)
            skipItems(1)

            val item = awaitItem()
            assertEquals(true, item.errorVisible)
            assertEquals(player1edited, item.player)
        }
    }
}
