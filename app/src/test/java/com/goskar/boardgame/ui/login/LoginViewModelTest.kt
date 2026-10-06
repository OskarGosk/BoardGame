package com.goskar.boardgame.ui.login

import app.cash.turbine.test
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.data.useCase.GetLocalSnapshotUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import io.mockk.coVerify
import com.goskar.boardgame.data.rest.RequestResult
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var userSession: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel
    private lateinit var getSnapshot: GetLocalSnapshotUseCase
    private lateinit var clearDb: ClearDbUseCase
    private lateinit var syncState: SyncStateRepository

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        // LoginViewModel grabs FirebaseAuth.getInstance() in its init, so we mock the static.
        mockkStatic(FirebaseAuth::class)
        auth = mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns auth
        every { auth.currentUser } returns null

        userSession = mockk(relaxed = true)
        getSnapshot = mockk()
        clearDb = mockk()
        syncState = mockk(relaxed = true)
        viewModel = LoginViewModel(userSession, getSnapshot, clearDb, syncState)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(FirebaseAuth::class)
    }

    @Test
    fun questAccount_setsGuestStateAndEmitsLoggedInOrGuest() = runTest(testDispatcher) {
        coEvery { userSession.logIn(any()) } returns RequestResult.Success(true)

        viewModel.events.test {
            viewModel.questAccount()
            assertEquals(LoginEvent.LoggedInOrGuest, awaitItem())
        }

        val state = viewModel.state.value
        assertEquals("guest", state.login)
        assertEquals("guest", state.keyValue)
        assertEquals("guest", state.userUID)
    }

    @Test
    fun checkIfLoggedIn_noUser_guestSession_setsLoggedIn() = runTest(testDispatcher) {
        every { auth.currentUser } returns null
        coEvery { userSession.getCurrentSession() } returns
            User(email = null, token = null, userUID = "guest")

        viewModel.checkIfLoggedIn()

        val state = viewModel.state.value
        assertEquals(true, state.isLoggedIn)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun checkIfLoggedIn_noUser_noGuestSession_setsNotLoggedIn() = runTest(testDispatcher) {
        every { auth.currentUser } returns null
        coEvery { userSession.getCurrentSession() } returns null
        coEvery { userSession.logout() } returns RequestResult.Success(true)

        viewModel.checkIfLoggedIn()

        val state = viewModel.state.value
        assertEquals(false, state.isLoggedIn)
        assertEquals(false, state.isLoading)
    }

    private val emptySnapshot = LocalSnapshot(emptyList(), emptyList(), emptyList(), emptyList())
    private val guestSnapshot = LocalSnapshot(
        games = listOf(
            Game(
                name = "Root", expansion = false, cooperate = false, baseGame = "",
                minPlayer = "1", maxPlayer = "4", games = 1, id = "g1",
            )
        ),
        players = emptyList(),
        history = emptyList(),
        historyExpansions = emptyList(),
    )

    @Test
    fun continueAfterSignIn_noLocalData_savesSessionWithoutAsking() = runTest(testDispatcher) {
        coEvery { getSnapshot() } returns emptySnapshot
        coEvery { userSession.logIn(any()) } returns RequestResult.Success(true)

        viewModel.events.test {
            viewModel.continueAfterSignIn()
            assertEquals(
                LoginEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS, mergeLocalData = false),
                awaitItem(),
            )
        }
        assertNull(viewModel.state.value.pendingLocalData)
        coVerify(exactly = 1) { userSession.logIn(any()) }
    }

    @Test
    fun continueAfterSignIn_localDataPresent_asksAndSavesNothingYet() = runTest(testDispatcher) {
        coEvery { getSnapshot() } returns guestSnapshot

        viewModel.continueAfterSignIn()

        assertEquals(LocalDataSummary(gameCount = 1, playerCount = 0, sessionCount = 0), viewModel.state.value.pendingLocalData)
        coVerify(exactly = 0) { userSession.logIn(any()) }
    }

    @Test
    fun mergeLocalDataIntoAccount_savesSessionAndFlagsMerge() = runTest(testDispatcher) {
        coEvery { getSnapshot() } returns guestSnapshot
        coEvery { userSession.logIn(any()) } returns RequestResult.Success(true)
        viewModel.continueAfterSignIn()

        viewModel.events.test {
            viewModel.mergeLocalDataIntoAccount()
            assertEquals(
                LoginEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS, mergeLocalData = true),
                awaitItem(),
            )
        }
        assertNull(viewModel.state.value.pendingLocalData)
        coVerify(exactly = 0) { clearDb() }
        coVerify(exactly = 1) { syncState.clear() }
    }

    @Test
    fun discardLocalData_clearsDbThenSignsInWithoutMerge() = runTest(testDispatcher) {
        coEvery { getSnapshot() } returns guestSnapshot
        coEvery { clearDb() } returns true
        coEvery { userSession.logIn(any()) } returns RequestResult.Success(true)
        viewModel.continueAfterSignIn()

        viewModel.events.test {
            viewModel.discardLocalData()
            assertEquals(
                LoginEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS, mergeLocalData = false),
                awaitItem(),
            )
        }
        coVerify(exactly = 1) { clearDb() }
    }

    @Test
    fun discardLocalData_clearFails_keepsAskingAndReportsError() = runTest(testDispatcher) {
        coEvery { getSnapshot() } returns guestSnapshot
        coEvery { clearDb() } returns false
        viewModel.continueAfterSignIn()

        viewModel.events.test {
            viewModel.discardLocalData()
            assertEquals(LoginEvent.ShowErrorMessage(null), awaitItem())
        }
        assertNotNull(viewModel.state.value.pendingLocalData)
        coVerify(exactly = 0) { userSession.logIn(any()) }
    }

    @Test
    fun cancelSignIn_leavesSessionAndDataUntouched() = runTest(testDispatcher) {
        coEvery { getSnapshot() } returns guestSnapshot
        viewModel.continueAfterSignIn()

        viewModel.cancelSignIn()

        assertNull(viewModel.state.value.pendingLocalData)
        coVerify(exactly = 0) { userSession.logIn(any()) }
        coVerify(exactly = 0) { clearDb() }
    }
}
