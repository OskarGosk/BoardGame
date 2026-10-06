package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.repository.syncState.SyncState
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetSyncStatusUseCaseTest {

    private lateinit var getSnapshot: GetLocalSnapshotUseCase
    private lateinit var syncState: SyncStateRepository
    private lateinit var useCase: GetSyncStatusUseCase

    private val withData = LocalSnapshot(
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
    private val empty = LocalSnapshot(emptyList(), emptyList(), emptyList(), emptyList())

    @Before
    fun setUp() {
        getSnapshot = mockk()
        syncState = mockk()
        useCase = GetSyncStatusUseCase(getSnapshot, syncState)
    }

    @Test
    fun emptyLocalData_isNeverUnsynced() = runTest {
        coEvery { getSnapshot() } returns empty
        coEvery { syncState.get() } returns SyncState()

        assertFalse(useCase().hasUnsyncedChanges)
    }

    @Test
    fun dataThatWasNeverSynced_isUnsynced() = runTest {
        coEvery { getSnapshot() } returns withData
        coEvery { syncState.get() } returns SyncState()

        assertTrue(useCase().hasUnsyncedChanges)
    }

    @Test
    fun dataMatchingLastSync_isSyncedAndReportsTime() = runTest {
        coEvery { getSnapshot() } returns withData
        coEvery { syncState.get() } returns SyncState(syncedFingerprint = withData.fingerprint(), lastSyncedAt = 99L)

        val status = useCase()

        assertFalse(status.hasUnsyncedChanges)
        assertEquals(99L, status.lastSyncedAt)
    }

    @Test
    fun dataChangedSinceLastSync_isUnsynced() = runTest {
        val changed = withData.copy(games = withData.games.map { it.copy(games = 2) })
        coEvery { getSnapshot() } returns changed
        coEvery { syncState.get() } returns SyncState(syncedFingerprint = withData.fingerprint(), lastSyncedAt = 99L)

        assertTrue(useCase().hasUnsyncedChanges)
    }
}
