package com.goskar.boardgame.ui.screens.home.viewmodel

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.GuestBackupMeta
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetThreeRecentSessionUseCase
import com.goskar.boardgame.data.useCase.RecentSession
import com.goskar.boardgame.data.useCase.GuestRestoreUseCase
import com.goskar.boardgame.data.useCase.MarkLocalDataSyncedUseCase
import com.goskar.boardgame.data.useCase.UploadToCloudUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import com.goskar.boardgame.ui.screens.home.HomeViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var getAllGameUseCase: GetAllGameUseCase
    private lateinit var userSession: UserRepository
    private lateinit var playerDbRepository: PlayerDbRepository
    private lateinit var mePlayerRepository: MePlayerRepository
    private lateinit var api: BoardGameFirebaseDataRepository
    private lateinit var addAllGameToDb: UpsertAllGameUseCase
    private lateinit var addAllPlayerToDb: UpsertAllPlayerUseCase
    private lateinit var addAllHistoryToDb: UpsertAllHistoryGameUseCase
    private lateinit var addAllHistoryGameExpansionToDb: UpsertAllHistoryGameExpansionUseCase
    private lateinit var guestRestore: GuestRestoreUseCase
    private lateinit var markLocalDataSynced: MarkLocalDataSyncedUseCase
    private lateinit var uploadToCloud: UploadToCloudUseCase
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
        api,
        addAllGameToDb,
        addAllPlayerToDb,
        addAllHistoryToDb,
        addAllHistoryGameExpansionToDb,
        guestRestore,
        markLocalDataSynced,
        uploadToCloud,
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
        api = mockk(relaxed = true)
        coEvery { playerDbRepository.getAllPlayer() } returns RequestResult.Success(emptyList())
        addAllGameToDb = mockk(relaxed = true)
        addAllPlayerToDb = mockk(relaxed = true)
        addAllHistoryToDb = mockk(relaxed = true)
        addAllHistoryGameExpansionToDb = mockk(relaxed = true)
        guestRestore = mockk(relaxed = true)
        markLocalDataSynced = mockk(relaxed = true)
        uploadToCloud = mockk(relaxed = true)
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
            RecentSession("Wingspan", "2024", listOf("AM", "B"), "Winner: Alex M."),
            RecentSession("Root", "2024", listOf("AM", "B"), "Winner: Bob"),
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
        assertEquals("Alex M.", state.userName)
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
        assertEquals("Alex", state.userName)
        assertEquals("—", state.winRatio)
    }

    @Test
    fun load_guestSession_userNameIsGuest() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { userSession.getCurrentSession() } returns User(email = null, token = null, userUID = "guest")

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        assertEquals("Guest", viewModel.state.value.userName)
        assertEquals("0", viewModel.state.value.totalGames)
        assertEquals("—", viewModel.state.value.mostPlayed)
    }


    @Test
    fun load_firstLogin_syncsFromCloud() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { userSession.getCurrentSession() } returns null
        coEvery { api.getAllGame() } returns RequestResult.Error(Throwable("offline"))

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = true)

        coVerify(exactly = 1) { api.getAllGame() }
    }

    @Test
    fun load_notFirstLogin_doesNotSyncFromCloud() = runTest(testDispatcher) {
        coEvery { getAllGameUseCase.invoke() } returns emptyList()
        coEvery { userSession.getCurrentSession() } returns null

        val viewModel = buildViewModel()
        viewModel.load(firstLogin = false)

        coVerify(exactly = 0) { api.getAllGame() }
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

    private fun stubSuccessfulCloudDownload() {
        coEvery { api.getAllGame() } returns RequestResult.Success(emptyList())
        coEvery { api.getAllPlayer() } returns RequestResult.Success(emptyList())
        coEvery { api.getAllHistoryGame() } returns RequestResult.Success(emptyList())
        coEvery { api.getAllHistoryGameExpansion() } returns RequestResult.Success(emptyList())
        coEvery { addAllGameToDb.invoke(any()) } returns true
        coEvery { addAllPlayerToDb.invoke(any()) } returns true
        coEvery { addAllHistoryToDb.invoke(any()) } returns true
        coEvery { addAllHistoryGameExpansionToDb.invoke(any()) } returns true
    }

    @Test
    fun load_firstLogin_downloadSucceeds_marksDataSynced() = runTest(testDispatcher) {
        stubEmptyLibraryFor(null)
        stubSuccessfulCloudDownload()

        buildViewModel().load(firstLogin = true)

        coVerify(exactly = 1) { markLocalDataSynced() }
        coVerify(exactly = 0) { uploadToCloud() }
    }

    @Test
    fun load_firstLoginWithMergedLocalData_uploadsMergedDataAfterDownload() = runTest(testDispatcher) {
        stubEmptyLibraryFor(null)
        stubSuccessfulCloudDownload()

        buildViewModel().load(firstLogin = true, mergeLocalData = true)

        coVerify(exactly = 1) { uploadToCloud() }
        coVerify(exactly = 0) { markLocalDataSynced() }
    }

    @Test
    fun load_firstLoginWithMergedLocalData_downloadFails_neverOverwritesCloud() = runTest(testDispatcher) {
        stubEmptyLibraryFor(null)
        coEvery { api.getAllGame() } returns RequestResult.Error(Throwable("offline"))

        buildViewModel().load(firstLogin = true, mergeLocalData = true)

        coVerify(exactly = 0) { uploadToCloud() }
        coVerify(exactly = 0) { markLocalDataSynced() }
    }
}
