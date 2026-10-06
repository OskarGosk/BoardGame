package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.GuestBackupMeta
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.repository.guestBackup.GuestBackupInfo
import com.goskar.boardgame.data.repository.guestBackup.GuestBackupRepository
import com.goskar.boardgame.data.repository.syncState.SyncState
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.rest.RequestResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GuestRestoreUseCaseTest {

    private lateinit var getSnapshot: GetLocalSnapshotUseCase
    private lateinit var guestBackup: GuestBackupRepository
    private lateinit var syncState: SyncStateRepository
    private lateinit var upsertGames: UpsertAllGameUseCase
    private lateinit var upsertPlayers: UpsertAllPlayerUseCase
    private lateinit var upsertHistory: UpsertAllHistoryGameUseCase
    private lateinit var upsertExpansions: UpsertAllHistoryGameExpansionUseCase
    private lateinit var useCase: GuestRestoreUseCase

    private val meta = GuestBackupMeta(updatedAt = 10L, gameCount = 1, playerCount = 0, sessionCount = 0)
    private val empty = LocalSnapshot(emptyList(), emptyList(), emptyList(), emptyList())
    private val backedUp = LocalSnapshot(
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

    @Before
    fun setUp() {
        getSnapshot = mockk()
        guestBackup = mockk()
        syncState = mockk(relaxed = true)
        upsertGames = mockk()
        upsertPlayers = mockk()
        upsertHistory = mockk()
        upsertExpansions = mockk()
        useCase = GuestRestoreUseCase(
            getSnapshot, guestBackup, syncState, upsertGames, upsertPlayers, upsertHistory, upsertExpansions,
        )
        coEvery { syncState.get() } returns SyncState()
    }

    @Test
    fun findOffer_alreadyResolved_doesNotAskCloud() = runTest {
        coEvery { syncState.get() } returns SyncState(guestRestoreResolved = true)

        assertNull(useCase.findOffer())
        coVerify(exactly = 0) { guestBackup.getBackupInfo() }
    }

    @Test
    fun findOffer_localDataExists_neverOffersAndStopsAsking() = runTest {
        coEvery { getSnapshot() } returns backedUp

        assertNull(useCase.findOffer())
        coVerify(exactly = 1) { syncState.markGuestRestoreResolved() }
        coVerify(exactly = 0) { guestBackup.getBackupInfo() }
    }

    @Test
    fun findOffer_backupAvailable_returnsItsMeta() = runTest {
        coEvery { getSnapshot() } returns empty
        coEvery { guestBackup.getBackupInfo() } returns RequestResult.Success(GuestBackupInfo.Available(meta))

        assertEquals(meta, useCase.findOffer())
    }

    @Test
    fun findOffer_noBackup_stopsAsking() = runTest {
        coEvery { getSnapshot() } returns empty
        coEvery { guestBackup.getBackupInfo() } returns RequestResult.Success(GuestBackupInfo.None)

        assertNull(useCase.findOffer())
        coVerify(exactly = 1) { syncState.markGuestRestoreResolved() }
    }

    @Test
    fun findOffer_offline_asksAgainNextTime() = runTest {
        coEvery { getSnapshot() } returns empty
        coEvery { guestBackup.getBackupInfo() } returns RequestResult.Error(Throwable("offline"))

        assertNull(useCase.findOffer())
        coVerify(exactly = 0) { syncState.markGuestRestoreResolved() }
    }

    @Test
    fun restore_success_savesEverythingAndMarksSynced() = runTest {
        coEvery { guestBackup.download() } returns RequestResult.Success(backedUp)
        coEvery { upsertGames(any()) } returns true
        coEvery { upsertPlayers(any()) } returns true
        coEvery { upsertHistory(any()) } returns true
        coEvery { upsertExpansions(any()) } returns true
        coEvery { getSnapshot() } returns backedUp

        assertTrue(useCase.restore())
        coVerify(exactly = 1) { syncState.markSynced(backedUp.fingerprint()) }
        coVerify(exactly = 1) { syncState.markGuestRestoreResolved() }
    }

    @Test
    fun restore_downloadFails_keepsOfferOpen() = runTest {
        coEvery { guestBackup.download() } returns RequestResult.Error(Throwable("offline"))

        assertFalse(useCase.restore())
        coVerify(exactly = 0) { syncState.markGuestRestoreResolved() }
    }

    @Test
    fun restore_saveFails_doesNotMarkSynced() = runTest {
        coEvery { guestBackup.download() } returns RequestResult.Success(backedUp)
        coEvery { upsertGames(any()) } returns false

        assertFalse(useCase.restore())
        coVerify(exactly = 0) { syncState.markSynced(any()) }
    }
}
