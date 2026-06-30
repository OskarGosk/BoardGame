package com.goskar.boardgame.ui.login

import app.cash.turbine.test
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.user.UserRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var userSession: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel

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
        viewModel = LoginViewModel(userSession)
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
}
