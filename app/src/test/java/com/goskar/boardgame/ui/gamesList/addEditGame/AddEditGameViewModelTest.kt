package com.goskar.boardgame.ui.gamesList.addEditGame

import android.content.Context
import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditGameViewModelTest {

    private lateinit var gameRepo: GameDbRepository
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var context: Context
    private lateinit var viewModel: AddEditGameViewModel

    private val baseGame = createGame(name = "Wingspan", id = "base-1", expansion = false)
    private val expansionGame = createGame(name = "Wingspan: Europe", id = "exp-1", expansion = true)

    private fun createGame(
        name: String,
        id: String,
        expansion: Boolean,
        games: Int = 0
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
        getAllGameUseCase = mockk()
        // Context is only touched when uri is non-empty; tests keep uri empty.
        context = mockk(relaxed = true)

        coEvery { getAllGameUseCase.invoke() } returns listOf(baseGame, expansionGame)

        viewModel = AddEditGameViewModel(gameRepo, getAllGameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // init — base game list excludes expansions
    // -------------------------------------------------------------------------

    @Test
    fun init_allBaseGame_excludesExpansions() {
        assertEquals(listOf(baseGame), viewModel.allBaseGame.value)
    }

    // -------------------------------------------------------------------------
    // validateAddEitGame — insert vs edit branch (driven by id)
    // -------------------------------------------------------------------------

    @Test
    fun validateAddEitGame_nullId_insertsNewGameWithGeneratedId() = runTest(testDispatcher) {
        val saved = slot<Game>()
        coEvery { gameRepo.insertGame(capture(saved)) } returns RequestResult.Success(true)

        viewModel.updateName("Chess")
        viewModel.updateCameraUri("")
        viewModel.validateAddEitGame(context)

        coVerify(exactly = 1) { gameRepo.insertGame(any()) }
        coVerify(exactly = 0) { gameRepo.editGame(any()) }
        assertEquals("Chess", saved.captured.name)
        assertTrue(saved.captured.id.isNotBlank()) // UUID generated when id is null
    }

    @Test
    fun validateAddEitGame_existingId_editsGameKeepingId() = runTest(testDispatcher) {
        val saved = slot<Game>()
        coEvery { gameRepo.editGame(capture(saved)) } returns RequestResult.Success(true)

        viewModel.updateDataForEditGame(createGame(name = "Chess", id = "existing-id", expansion = false))
        viewModel.updateCameraUri("")
        viewModel.validateAddEitGame(context)

        coVerify(exactly = 1) { gameRepo.editGame(any()) }
        coVerify(exactly = 0) { gameRepo.insertGame(any()) }
        assertEquals("existing-id", saved.captured.id)
    }

    // -------------------------------------------------------------------------
    // validateAddEitGame — expansion rule:
    // expansion is only persisted as true when a base game is also set
    // -------------------------------------------------------------------------

    @Test
    fun validateAddEitGame_expansionWithBaseGame_persistsAsExpansion() = runTest(testDispatcher) {
        val saved = slot<Game>()
        coEvery { gameRepo.insertGame(capture(saved)) } returns RequestResult.Success(true)

        viewModel.updateName("Exp")
        viewModel.updateExpansion()
        viewModel.updateBaseBase("Wingspan", "base-1")
        viewModel.updateCameraUri("")
        viewModel.validateAddEitGame(context)

        assertEquals(true, saved.captured.expansion)
    }

    @Test
    fun validateAddEitGame_expansionWithoutBaseGame_persistsAsBaseGame() = runTest(testDispatcher) {
        val saved = slot<Game>()
        coEvery { gameRepo.insertGame(capture(saved)) } returns RequestResult.Success(true)

        viewModel.updateName("Exp")
        viewModel.updateExpansion()
        viewModel.updateBaseBase(null, null)
        viewModel.updateCameraUri("")
        viewModel.validateAddEitGame(context)

        assertEquals(false, saved.captured.expansion)
    }

    // -------------------------------------------------------------------------
    // validateAddEitGame — result handling
    // -------------------------------------------------------------------------

    @Test
    fun validateAddEitGame_success_emitsSavedAndClearsProgress() = runTest(testDispatcher) {
        coEvery { gameRepo.insertGame(any()) } returns RequestResult.Success(true)

        viewModel.updateName("Chess")
        viewModel.updateCameraUri("")
        viewModel.events.test {
            viewModel.validateAddEitGame(context)
            assertEquals(
                AddEditEvent.SuccessAddEditGame(R.string.success_global, AppSnackBarType.SUCCESS),
                awaitItem()
            )
        }
        assertEquals(false, viewModel.state.value.inProgress)
    }

    @Test
    fun validateAddEitGame_error_showsErrorAndClearsProgress() = runTest(testDispatcher) {
        coEvery { gameRepo.insertGame(any()) } returns RequestResult.Error(Throwable("db error"))

        viewModel.updateName("Chess")
        viewModel.updateCameraUri("")
        viewModel.events.test {
            viewModel.validateAddEitGame(context)
            assertEquals(
                AddEditEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem()
            )
        }
        assertEquals(false, viewModel.state.value.inProgress)
    }
}
