package com.goskar.boardgame.ui.screens.home.viewmodel

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.GuestBackupMeta
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetThreeRecentSessionUseCase
import com.goskar.boardgame.data.useCase.RecentSession
import com.goskar.boardgame.data.useCase.GuestRestoreUseCase
import com.goskar.boardgame.data.useCase.SyncWithCloudUseCase
import com.goskar.boardgame.ui.screens.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var userSession: UserRepository
    private lateinit var playerDbRepository: PlayerDbRepository
    private lateinit var mePlayerRepository: MePlayerRepository
    private lateinit var guestRestore: GuestRestoreUseCase
    private lateinit var syncWithCloud: SyncWithCloudUseCase
    private lateinit var getThreeRecentSession: GetThreeRecentSessionUseCase

    private fun game(name: String, plays: Int, id: String) = Game(
        name = name, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = plays, id = id,
    )

    private fun player(name: String, games: Int, winRatio: Int, id: String) =
        Player(name = name, games = games, winRatio = winRatio, description = "", selected = false, id = id)

    private fun buildViewModel() = HomeViewModel(
        getAllGameUseCase,
        userSession,
        playerDbRepository,
        mePlayerRepository,
        guestRestore,
        syncWithCloud,
        getThreeRecentSession,
    )

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        getAllGameUseCase = mockk()
        userSession = mockk()
        playerDbRepository = mockk()
        mePlayerRepository = mockk(relaxed = true)
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(emptyList())
        guestRestore = mockk(relaxed = true)
        syncWithCloud = mockk()
        coEvery { syncWithCloud() } returns true
        coEvery { syncWithCloud.syncOnStartup() } returns false
        getThreeRecentSession = mockk()
        coEvery { getThreeRecentSession() } returns emptyList()
        coEvery { guestRestore.findOffer() } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun load_usesLinkedPlayerForNameAndWinRatio() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns listOf(
            game("Wingspan", plays = 5, id = "g1"),
            game("Root", plays = 2, id = "g2"),
        )
        coEvery { getThreeRecentSession() } returns listOf(
            RecentSession("s1", "Wingspan", "2024", listOf("AM", "B"), "Winner: Alex M."),
            RecentSession("s2", "Root", "2024", listOf("AM", "B"), "Winner: Bob"),
        )
        coEvery { userSession.getCurrentSession() } returns
            User(email = "alex@example.com", token = "t", userUID = "uid1")
        // 3 wins out of 4 plays -> 75%
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(
            listOf(player("Alex M.", games = 4, winRatio = 3, id = "p1"))
        )
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns "p1"

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        val state = viewModel.state.value
        assertEquals("2", state.totalGames)
        assertEquals("Wingspan", state.mostPlayed)
        assertEquals("75%", state.winRatio)
        assertEquals(2, state.recentSessions.size)
        assertEquals("Wingspan", state.recentSessions.first().gameName)
    }

    @Test
    fun load_noLinkedPlayer_winRatioIsDash() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { userSession.getCurrentSession() } returns
            User(email = "alex@example.com", token = "t", userUID = "uid1")
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        val state = viewModel.state.value
        assertEquals("—", state.winRatio)
    }

    @Test
    fun load_guestSession_showsEmptyTotals() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { userSession.getCurrentSession() } returns User(email = null, token = null, userUID = "guest")

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        assertEquals("0", viewModel.state.value.totalGames)
        assertEquals("—", viewModel.state.value.mostPlayed)
    }


    private fun stubEmptyLibraryFor(uid: String?) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { userSession.getCurrentSession() } returns
            uid?.let { User(email = null, token = "t", userUID = it) }
    }

    @Test
    fun load_guestWithBackup_offersRestore() = runTest(testDispatcher) {
        stubEmptyLibraryFor("guest")
        coEvery { guestRestore.findOffer() } returns
            GuestBackupMeta(updatedAt = 1L, gameCount = 3, playerCount = 2, sessionCount = 5)

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        val offer = viewModel.state.value.guestRestoreOffer
        assertNotNull(offer)
        assertEquals(3, offer!!.gameCount)
        assertEquals(5, offer.sessionCount)
    }

    @Test
    fun load_accountUser_neverChecksGuestBackup() = runTest(testDispatcher) {
        stubEmptyLibraryFor("uid1")
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        coVerify(exactly = 0) { guestRestore.findOffer() }
        assertNull(viewModel.state.value.guestRestoreOffer)
    }

    @Test
    fun restoreGuestBackup_success_clearsOffer() = runTest(testDispatcher) {
        stubEmptyLibraryFor("guest")
        coEvery { guestRestore.findOffer() } returns GuestBackupMeta(updatedAt = 1L, gameCount = 1, sessionCount = 1)
        coEvery { guestRestore.restore() } returns true

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)
        viewModel.restoreGuestBackup()

        assertNull(viewModel.state.value.guestRestoreOffer)
        coVerify(exactly = 1) { guestRestore.restore() }
    }

    @Test
    fun restoreGuestBackup_failure_keepsOfferAndMarksFailed() = runTest(testDispatcher) {
        stubEmptyLibraryFor("guest")
        coEvery { guestRestore.findOffer() } returns GuestBackupMeta(updatedAt = 1L, gameCount = 1, sessionCount = 1)
        coEvery { guestRestore.restore() } returns false

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)
        viewModel.restoreGuestBackup()

        val offer = viewModel.state.value.guestRestoreOffer
        assertNotNull(offer)
        assertTrue(offer!!.failed)
        assertFalse(offer.inProgress)
    }

    @Test
    fun declineGuestRestore_clearsOfferAndRemembersChoice() = runTest(testDispatcher) {
        stubEmptyLibraryFor("guest")
        coEvery { guestRestore.findOffer() } returns GuestBackupMeta(updatedAt = 1L, gameCount = 1, sessionCount = 1)

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)
        viewModel.declineGuestRestore()

        assertNull(viewModel.state.value.guestRestoreOffer)
        coVerify(exactly = 1) { guestRestore.decline() }
    }

    @Test
    fun load_firstLoginWithAccount_syncsBeforeComputingStats() = runTest(testDispatcher) {
        stubEmptyLibraryFor("uid1")
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null

        buildViewModel().load(firstLogin = true)

        coVerifyOrder {
            syncWithCloud()
            getAllGameUseCase()
        }
        coVerify(exactly = 0) { syncWithCloud.syncOnStartup() }
    }

    @Test
    fun load_firstLoginSyncFails_stillShowsLocalLibrary() = runTest(testDispatcher) {
        stubEmptyLibraryFor("uid1")
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null
        coEvery { syncWithCloud() } returns false

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = true)

        assertFalse(viewModel.state.value.isLoading)
        assertEquals("0", viewModel.state.value.totalGames)
    }

    @Test
    fun load_guest_neverSyncs() = runTest(testDispatcher) {
        stubEmptyLibraryFor("guest")

        buildViewModel().load(firstLogin = true)
        buildViewModel().load(firstLogin = false)

        coVerify(exactly = 0) { syncWithCloud() }
        coVerify(exactly = 0) { syncWithCloud.syncOnStartup() }
    }

    @Test
    fun load_withoutSession_neverSyncs() = runTest(testDispatcher) {
        stubEmptyLibraryFor(null)

        buildViewModel().load(firstLogin = false)

        coVerify(exactly = 0) { syncWithCloud.syncOnStartup() }
    }

    @Test
    fun load_appStart_showsLocalStatsThenRefreshesAfterSync() = runTest(testDispatcher) {
        stubEmptyLibraryFor("uid1")
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null
        coEvery { getAllGameUseCase.invoke() } returnsMany listOf(
            emptyList(),
            listOf(game("Root", plays = 1, id = "g1")),
        )
        coEvery { syncWithCloud.syncOnStartup() } returns true

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        assertEquals("1", viewModel.state.value.totalGames)
        coVerify(exactly = 2) { getAllGameUseCase() }
        coVerify(exactly = 0) { syncWithCloud() }
    }

    @Test
    fun load_startupSyncAlreadyDone_computesStatsOnce() = runTest(testDispatcher) {
        stubEmptyLibraryFor("uid1")
        coEvery { mePlayerRepository.getLinkedPlayerId("uid1") } returns null

        buildViewModel().load(firstLogin = false)

        coVerify(exactly = 1) { syncWithCloud.syncOnStartup() }
        coVerify(exactly = 1) { getAllGameUseCase() }
    }

    @Ignore("TODO: sprawdz, ze dla polaczonego gracza state.userName to UserName.Named('Alex M.')")
    @Test
    fun load_usesLinkedPlayer_userNameIsNamed() {
    }

    @Ignore("TODO: sprawdz, ze bez polaczonego gracza state.userName to UserName.Player")
    @Test
    fun load_noLinkedPlayer_userNameIsPlayer() {
    }

    @Ignore("TODO: sprawdz, ze dla sesji goscia state.userName to UserName.Guest")
    @Test
    fun load_guestSession_userNameIsGuest() {
    }
}
