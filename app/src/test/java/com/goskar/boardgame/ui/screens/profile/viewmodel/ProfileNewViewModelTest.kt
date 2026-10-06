package com.goskar.boardgame.ui.screens.profile.viewmodel

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import app.cash.turbine.test
import com.goskar.boardgame.R
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.BackupGuestUseCase
import com.goskar.boardgame.data.useCase.GetSyncStatusUseCase
import com.goskar.boardgame.data.useCase.SyncStatus
import com.goskar.boardgame.data.useCase.UploadToCloudUseCase
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileNewViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var userSession: UserRepository
    private lateinit var playerDbRepository: PlayerDbRepository
    private lateinit var historyRepository: GamesHistoryDbRepository
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var mePlayerRepository: MePlayerRepository
    private lateinit var clearDbUseCase: ClearDbUseCase
    private lateinit var uploadToCloud: UploadToCloudUseCase
    private lateinit var backupGuest: BackupGuestUseCase
    private lateinit var getSyncStatus: GetSyncStatusUseCase
    private lateinit var syncState: SyncStateRepository

    private fun player(name: String, games: Int, winRatio: Int, id: String) =
        Player(name = name, games = games, winRatio = winRatio, description = "", selected = false, id = id)

    private fun game(id: String) = Game(
        name = id, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, id = id,
    )

    private fun history(id: String) =
        HistoryGame(gameName = "g", winner = "w", gameData = LocalDate.now(), listOfPlayer = emptyList(), description = "", id = id)

    private fun buildViewModel() = ProfileNewViewModel(
        userSession, playerDbRepository, historyRepository, mePlayerRepository, clearDbUseCase, uploadToCloud,
        backupGuest, getSyncStatus, syncState,
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        userSession = mockk()
        playerDbRepository = mockk()
        historyRepository = mockk()
        getAllGameUseCase = mockk()
        mePlayerRepository = mockk(relaxed = true)
        clearDbUseCase = mockk(relaxed = true)
        uploadToCloud = mockk(relaxed = true)
        backupGuest = mockk(relaxed = true)
        syncState = mockk(relaxed = true)
        getSyncStatus = mockk()
        coEvery { getSyncStatus() } returns SyncStatus(hasUnsyncedChanges = false, lastSyncedAt = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(FirebaseAuth::class)
    }

    private fun mockFirebaseAuth() {
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
        coEvery { userSession.logout() } returns RequestResult.Success(true)
    }

    private fun stubAccountSession() {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "a@b.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(emptyList())
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(emptyList())
    }

    private fun stubGuestSession() {
        coEvery { userSession.getCurrentSession() } returns User(email = null, token = "t", userUID = "guest")
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(emptyList())
    }

    @Test
    fun load_linkedPlayer_showsRealStats() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "alex@example.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(
            listOf(player("Alex M.", games = 4, winRatio = 3, id = "p1"))
        )
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns "p1"

        val viewModel = buildViewModel()
        viewModel.load()

        val state = viewModel.state.value
        assertFalse(state.isGuest)
        assertEquals("Alex M.", state.name)
        assertEquals("alex@example.com", state.subtitle)
        assertEquals("4", state.gamesLogged)
        assertEquals("75%", state.winRate)
    }

    @Test
    fun load_guest_showsGlobalAggregates() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = null, token = "t", userUID = "guest")
        coEvery { getAllGameUseCase.invoke() } returns listOf(game("g1"), game("g2"), game("g3"))
        coEvery { historyRepository.getAllHistoryGame() } returns RequestResult.Success(
            listOf(history("h1"), history("h2"))
        )

        val viewModel = buildViewModel()
        viewModel.load()

        val state = viewModel.state.value
        assertTrue(state.isGuest)
        assertEquals("Guest", state.name)
        assertEquals("2", state.gamesLogged)
        assertEquals("—", state.winRate)
    }

    @Test
    fun forceSync_success_emitsSuccessAndUpdatesState() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "a@b.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(emptyList())
        coEvery { uploadToCloud() } returns true

        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.forceSync()
            assertEquals(
                ProfileEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS),
                awaitItem(),
            )
        }
        assertEquals(false, viewModel.state.value.syncing)
        assertEquals("Synced just now", viewModel.state.value.lastSynced)
    }

    @Test
    fun forceSync_failure_emitsError() = runTest(testDispatcher) {
        coEvery { userSession.getCurrentSession() } returns
            User(email = "a@b.com", token = "t", userUID = "uid1")
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(emptyList())
        coEvery { uploadToCloud() } returns false

        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.forceSync()
            assertEquals(
                ProfileEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR),
                awaitItem(),
            )
        }
    }

    @Test
    fun load_exposesSyncStatus() = runTest(testDispatcher) {
        stubAccountSession()
        coEvery { getSyncStatus() } returns SyncStatus(hasUnsyncedChanges = true, lastSyncedAt = 1234L)

        val viewModel = buildViewModel()
        viewModel.load()

        assertTrue(viewModel.state.value.hasUnsyncedChanges)
        assertEquals(1234L, viewModel.state.value.lastSyncedAt)
    }

    @Test
    fun forceSync_guest_backsUpInsteadOfUploadingToAccount() = runTest(testDispatcher) {
        stubGuestSession()
        coEvery { backupGuest() } returns true

        val viewModel = buildViewModel()
        viewModel.load()
        viewModel.forceSync()

        coVerify(exactly = 1) { backupGuest() }
        coVerify(exactly = 0) { uploadToCloud() }
    }

    @Test
    fun load_guest_isGuestButUnlinkedAccountIsNot() = runTest(testDispatcher) {
        stubGuestSession()
        val guest = buildViewModel().apply { load() }
        assertTrue(guest.state.value.isGuest)

        stubAccountSession()
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null
        val unlinked = buildViewModel().apply { load() }
        assertFalse(unlinked.state.value.isGuest)
    }

    @Test
    fun requestSignOut_unsyncedData_asksFirstAndKeepsData() = runTest(testDispatcher) {
        stubAccountSession()
        coEvery { getSyncStatus() } returns SyncStatus(hasUnsyncedChanges = true, lastSyncedAt = null)

        val viewModel = buildViewModel()
        viewModel.requestSignOut()

        assertEquals(SignOutPrompt.UnsyncedData, viewModel.state.value.signOutPrompt)
        assertFalse(viewModel.state.value.signedOut)
        coVerify(exactly = 0) { clearDbUseCase.invoke() }
    }

    @Test
    fun requestSignOut_allSynced_signsOutAndClearsLocalState() = runTest(testDispatcher) {
        mockFirebaseAuth()
        stubAccountSession()

        val viewModel = buildViewModel()
        viewModel.requestSignOut()

        assertTrue(viewModel.state.value.signedOut)
        coVerify(exactly = 1) { clearDbUseCase.invoke() }
        coVerify(exactly = 1) { syncState.clear() }
    }

    @Test
    fun syncAndSignOut_syncSucceeds_signsOut() = runTest(testDispatcher) {
        mockFirebaseAuth()
        stubAccountSession()
        coEvery { uploadToCloud() } returns true

        val viewModel = buildViewModel()
        viewModel.load()
        viewModel.syncAndSignOut()

        assertTrue(viewModel.state.value.signedOut)
        assertEquals(SignOutPrompt.None, viewModel.state.value.signOutPrompt)
    }

    @Test
    fun syncAndSignOut_syncFails_staysSignedInAndWarns() = runTest(testDispatcher) {
        stubAccountSession()
        coEvery { uploadToCloud() } returns false

        val viewModel = buildViewModel()
        viewModel.load()
        viewModel.syncAndSignOut()

        assertFalse(viewModel.state.value.signedOut)
        assertEquals(SignOutPrompt.SyncFailed, viewModel.state.value.signOutPrompt)
        coVerify(exactly = 0) { clearDbUseCase.invoke() }
    }

    @Test
    fun signOutWithoutSync_signsOutEvenWithUnsyncedData() = runTest(testDispatcher) {
        mockFirebaseAuth()
        stubAccountSession()
        coEvery { getSyncStatus() } returns SyncStatus(hasUnsyncedChanges = true, lastSyncedAt = null)

        val viewModel = buildViewModel()
        viewModel.requestSignOut()
        viewModel.signOutWithoutSync()

        assertTrue(viewModel.state.value.signedOut)
        assertEquals(SignOutPrompt.None, viewModel.state.value.signOutPrompt)
        coVerify(exactly = 1) { clearDbUseCase.invoke() }
    }

    @Test
    fun dismissSignOutPrompt_keepsSessionAndData() = runTest(testDispatcher) {
        stubAccountSession()
        coEvery { getSyncStatus() } returns SyncStatus(hasUnsyncedChanges = true, lastSyncedAt = null)

        val viewModel = buildViewModel()
        viewModel.requestSignOut()
        viewModel.dismissSignOutPrompt()

        assertEquals(SignOutPrompt.None, viewModel.state.value.signOutPrompt)
        assertFalse(viewModel.state.value.signedOut)
    }
}
