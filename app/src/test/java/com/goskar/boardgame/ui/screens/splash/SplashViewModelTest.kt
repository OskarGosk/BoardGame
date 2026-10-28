package com.goskar.boardgame.ui.screens.splash

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.goskar.boardgame.data.repository.user.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        mockkStatic(FirebaseAuth::class)
        auth = mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns auth
        every { auth.currentUser } returns null
        userRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(FirebaseAuth::class)
    }

    @Test
    fun noDestination_untilTheSplashDelayPasses() = runTest(testDispatcher) {
        coEvery { userRepository.isLoggedIn() } returns true
        val viewModel = SplashViewModel(userRepository)

        advanceTimeBy(999)

        assertNull(viewModel.destination.value)
    }

    @Test
    fun loggedInLocally_goesHome() = runTest(testDispatcher) {
        coEvery { userRepository.isLoggedIn() } returns true
        val viewModel = SplashViewModel(userRepository)

        advanceUntilIdle()

        assertEquals(SplashDestination.Home, viewModel.destination.value)
    }

    @Test
    fun firebaseUser_goesHome_evenWithoutLocalSession() = runTest(testDispatcher) {
        val user = mockk<FirebaseUser> { every { isAnonymous } returns false }
        every { auth.currentUser } returns user
        coEvery { userRepository.isLoggedIn() } returns false
        val viewModel = SplashViewModel(userRepository)

        advanceUntilIdle()

        assertEquals(SplashDestination.Home, viewModel.destination.value)
    }

    @Test
    fun anonymousFirebaseUser_withoutLocalSession_goesToLogin() = runTest(testDispatcher) {
        val user = mockk<FirebaseUser> { every { isAnonymous } returns true }
        every { auth.currentUser } returns user
        coEvery { userRepository.isLoggedIn() } returns false
        val viewModel = SplashViewModel(userRepository)

        advanceUntilIdle()

        assertEquals(SplashDestination.Login, viewModel.destination.value)
    }

    @Test
    fun destination_isStillAvailable_whenTheScreenStartsObservingLate() = runTest(testDispatcher) {
        // Regression: the old SharedFlow event was dropped if the screen was being recreated
        // (theme change / rotation) at the moment it fired, leaving the app stuck on the splash.
        coEvery { userRepository.isLoggedIn() } returns false
        val viewModel = SplashViewModel(userRepository)
        advanceUntilIdle()

        val seenByRecreatedScreen = viewModel.destination.value

        assertEquals(SplashDestination.Login, seenByRecreatedScreen)
    }
}
