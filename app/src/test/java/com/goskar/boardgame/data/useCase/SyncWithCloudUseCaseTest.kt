package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.db.SyncDao
import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.DeletedRecord
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.rest.RequestResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SyncWithCloudUseCaseTest {

    private lateinit var api: BoardGameFirebaseDataRepository
    private lateinit var migrateLegacy: MigrateLegacyCloudDataUseCase
    private lateinit var getSnapshot: GetLocalSnapshotUseCase
    private lateinit var syncDao: SyncDao
    private lateinit var syncState: SyncStateRepository
    private lateinit var useCase: SyncWithCloudUseCase

    private fun game(id: String) = Game(
        name = id, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, id = id,
    )

    private fun snapshot(vararg games: Game) = LocalSnapshot(games.toList(), emptyList(), emptyList(), emptyList())

    @Before
    fun setUp() {
        api = mockk()
        migrateLegacy = mockk()
        getSnapshot = mockk()
        syncDao = mockk(relaxed = true)
        syncState = mockk(relaxed = true)
        coEvery { migrateLegacy() } returns true
        coEvery { syncDao.getAllDeleted() } returns emptyList()
        coEvery { api.uploadAll(any()) } returns RequestResult.Success(true)
        useCase = SyncWithCloudUseCase(api, migrateLegacy, getSnapshot, syncDao, syncState)
    }

    @Test
    fun bothSidesHaveNewRecords_storesAndUploadsTheUnion() = runTest {
        coEvery { getSnapshot() } returns snapshot(game("local"))
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData(game = mapOf("remote" to game("remote"))))
        val uploaded = slot<CloudUserData>()
        coEvery { api.uploadAll(capture(uploaded)) } returns RequestResult.Success(true)

        assertTrue(useCase())

        assertEquals(setOf("local", "remote"), uploaded.captured.game?.keys)
        coVerify(exactly = 1) {
            syncDao.replaceAll(match { list -> list.map { it.id }.toSet() == setOf("local", "remote") }, any(), any(), any(), any())
        }
        coVerify(exactly = 1) { syncState.markSynced(any()) }
    }

    @Test
    fun cloudAlreadyHasEverything_onlyUpdatesLocalCopy() = runTest {
        coEvery { getSnapshot() } returns snapshot()
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData(game = mapOf("g1" to game("g1"))))

        assertTrue(useCase())

        coVerify(exactly = 1) { syncDao.replaceAll(any(), any(), any(), any(), any()) }
        coVerify(exactly = 0) { api.uploadAll(any()) }
    }

    @Test
    fun nothingChanged_writesNothing() = runTest {
        coEvery { getSnapshot() } returns snapshot(game("g1"))
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData(game = mapOf("g1" to game("g1"))))

        assertTrue(useCase())

        coVerify(exactly = 0) { syncDao.replaceAll(any(), any(), any(), any(), any()) }
        coVerify(exactly = 0) { api.uploadAll(any()) }
        coVerify(exactly = 1) { syncState.markSynced(snapshot(game("g1")).fingerprint()) }
    }

    @Test
    fun localDeletion_isUploadedWithItsRecord() = runTest {
        coEvery { getSnapshot() } returns snapshot()
        coEvery { syncDao.getAllDeleted() } returns listOf(DeletedRecord("g1", DeletedRecord.GAME, deletedAt = 10))
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData(game = mapOf("g1" to game("g1"))))
        val uploaded = slot<CloudUserData>()
        coEvery { api.uploadAll(capture(uploaded)) } returns RequestResult.Success(true)

        assertTrue(useCase())

        assertEquals(emptyMap<String, Game>(), uploaded.captured.game)
        assertEquals(setOf("g1"), uploaded.captured.deleted?.keys)
    }

    @Test
    fun migrationFails_stopsBeforeDownloading() = runTest {
        coEvery { migrateLegacy() } returns false

        assertFalse(useCase())

        coVerify(exactly = 0) { api.getUserData() }
        coVerify(exactly = 0) { syncDao.replaceAll(any(), any(), any(), any(), any()) }
    }

    @Test
    fun downloadFails_leavesLocalDataAlone() = runTest {
        coEvery { api.getUserData() } returns RequestResult.Error(Throwable("offline"))

        assertFalse(useCase())

        coVerify(exactly = 0) { syncDao.replaceAll(any(), any(), any(), any(), any()) }
        coVerify(exactly = 0) { syncState.markSynced(any()) }
    }

    @Test
    fun uploadFails_keepsMergedLocalDataButIsNotMarkedSynced() = runTest {
        coEvery { getSnapshot() } returns snapshot(game("local"))
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData())
        coEvery { api.uploadAll(any()) } returns RequestResult.Error(Throwable("offline"))

        assertFalse(useCase())

        coVerify(exactly = 0) { syncState.markSynced(any()) }
    }

    @Test
    fun localWriteThrows_returnsFalseWithoutUploading() = runTest {
        coEvery { getSnapshot() } returns snapshot()
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData(game = mapOf("g1" to game("g1"))))
        coEvery { syncDao.replaceAll(any(), any(), any(), any(), any()) } throws IllegalStateException("disk full")

        assertFalse(useCase())

        coVerify(exactly = 0) { api.uploadAll(any()) }
    }

    @Test
    fun syncOnStartup_runsOnlyOncePerLaunch() = runTest {
        coEvery { getSnapshot() } returns snapshot()
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData())

        assertTrue(useCase.syncOnStartup())
        assertFalse(useCase.syncOnStartup())

        coVerify(exactly = 1) { api.getUserData() }
    }

    @Test
    fun syncOnStartup_skippedAfterAnEarlierSync() = runTest {
        coEvery { getSnapshot() } returns snapshot()
        coEvery { api.getUserData() } returns RequestResult.Success(CloudUserData())

        useCase()

        assertFalse(useCase.syncOnStartup())
        coVerify(exactly = 1) { api.getUserData() }
    }
}
