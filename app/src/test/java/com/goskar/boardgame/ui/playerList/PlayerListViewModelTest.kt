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

import org.junit.After
import org.junit.Before
import org.junit.Test

class PlayerListViewModelTest {

    @Before
    fun setUp() {
        // Dispatchers.setMain(UnconfinedTestDispatcher())
        // repository = mockk<PlayerDbRepository>()
        // viewModel  = PlayerListViewModel(repository)
    }

    @After
    fun tearDown() {
        // Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // getAllPlayer()
    // Emits isLoading=true, calls repository, then sets playerList or errorVisible
    // -------------------------------------------------------------------------

    @Test
    fun getAllPlayer_setsIsLoadingTrueBeforeRepositoryCall() {
        // Given: repository.getAllPlayer() is stubbed but will not return immediately
        //        (use a coEvery { } that suspends or just capture state mid-flight)
        // When:  getAllPlayer() is called
        // Then:  the first state emission has isLoading == true
        // Tip:   With Turbine, awaitItem() after triggering will catch the intermediate
        //        loading state before the result state.
    }

    @Test
    fun getAllPlayer_success_setsPlayerListAndClearsLoadingAndError() {
        // Given: repository.getAllPlayer() returns RequestResult.Success(listOf(player1, player2))
        // When:  getAllPlayer() is called
        // Then:  state.playerList == listOf(player1, player2)
        //        state.isLoading  == false
        //        state.errorVisible == false
    }

    @Test
    fun getAllPlayer_success_emptyList_setsEmptyPlayerList() {
        // Given: repository.getAllPlayer() returns RequestResult.Success(emptyList())
        // When:  getAllPlayer() is called
        // Then:  state.playerList == emptyList()
        //        state.isLoading  == false
    }

    @Test
    fun getAllPlayer_error_showsErrorAndClearsLoading() {
        // Given: repository.getAllPlayer() returns RequestResult.Error(Throwable("db error"))
        // When:  getAllPlayer() is called
        // Then:  state.errorVisible == true
        //        state.isLoading    == false
        //        state.playerList   is unchanged from before the call
    }

    // -------------------------------------------------------------------------
    // validateDeletePlayer(player)
    // Calls repository.deletePlayer(), on success sets successDeletePlayer=true
    // and triggers getAllPlayer() to refresh the list
    // -------------------------------------------------------------------------

    @Test
    fun validateDeletePlayer_success_setsSuccessFlagAndRefreshesList() {
        // Given: repository.deletePlayer() returns RequestResult.Success(Unit)
        //        repository.getAllPlayer() returns RequestResult.Success(updatedList)
        // When:  validateDeletePlayer(player) is called
        // Then:  state.successDeletePlayer == true
        //        state.errorVisible        == false
        //        repository.getAllPlayer() was called (verify with mockk: verify { })
    }

    @Test
    fun validateDeletePlayer_error_showsError() {
        // Given: repository.deletePlayer() returns RequestResult.Error(Throwable())
        // When:  validateDeletePlayer(player) is called
        // Then:  state.errorVisible == true
        //        state.successDeletePlayer is unchanged (false)
        //        repository.getAllPlayer() is NOT called after the error
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
    }

    @Test
    fun validateAddEditPlayer_newPlayer_success_clearsPlayerAndRefreshes() {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.insertPlayer(aPlayer) returns RequestResult.Success(Unit)
        //        repository.getAllPlayer() returns RequestResult.Success(listOf(aPlayer))
        // When:  validateAddEditPLayer(newPlayer = true) is called
        // Then:  state.player == null
        //        repository.insertPlayer() was called (not editPlayer())
        //        repository.getAllPlayer() was called
    }

    @Test
    fun validateAddEditPlayer_newPlayer_error_showsError() {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.insertPlayer(aPlayer) returns RequestResult.Error(Throwable())
        // When:  validateAddEditPLayer(newPlayer = true) is called
        // Then:  state.errorVisible == true
        //        state.player is unchanged (not cleared)
    }

    @Test
    fun validateAddEditPlayer_editExistingPlayer_callsEditNotInsert() {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.editPlayer(aPlayer) returns RequestResult.Success(Unit)
        //        repository.getAllPlayer() returns RequestResult.Success(listOf(aPlayer))
        // When:  validateAddEditPLayer(newPlayer = false) is called
        // Then:  repository.editPlayer() was called (not insertPlayer())
        //        state.player == null
    }

    @Test
    fun validateAddEditPlayer_editExistingPlayer_error_showsError() {
        // Given: viewModel.update(state.copy(player = aPlayer))
        //        repository.editPlayer(aPlayer) returns RequestResult.Error(Throwable())
        // When:  validateAddEditPLayer(newPlayer = false) is called
        // Then:  state.errorVisible == true
    }
}
