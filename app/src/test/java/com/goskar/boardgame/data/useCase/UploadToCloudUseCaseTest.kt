package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.models.Player
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

class UploadToCloudUseCaseTest {

    private lateinit var api: BoardGameFirebaseDataRepository
    private lateinit var getSnapshot: GetLocalSnapshotUseCase
    private lateinit var syncState: SyncStateRepository
    private lateinit var useCase: UploadToCloudUseCase

    private val snapshot = LocalSnapshot(
        games = listOf(
            Game(
                name = "Root", expansion = false, cooperate = false, baseGame = "",
                minPlayer = "1", maxPlayer = "4", games = 1, id = "g1",
            )
        ),
        players = listOf(Player(name = "Ala", games = 1, winRatio = 1, description = "", selected = false, id = "p1")),
        history = emptyList(),
        historyExpansions = emptyList(),
    )

    @Before
    fun setUp() {
        api = mockk()
        getSnapshot = mockk()
        syncState = mockk(relaxed = true)
        coEvery { getSnapshot() } returns snapshot
        useCase = UploadToCloudUseCase(api, getSnapshot, syncState)
    }

    @Test
    fun uploadsEveryCollectionInOneWriteAndMarksSynced() = runTest {
        val sent = slot<CloudUserData>()
        coEvery { api.uploadAll(capture(sent)) } returns RequestResult.Success(true)

        assertTrue(useCase())

        coVerify(exactly = 1) { api.uploadAll(any()) }
        assertEquals(setOf("g1"), sent.captured.game?.keys)
        assertEquals(setOf("p1"), sent.captured.player?.keys)
        assertEquals(emptyMap<String, Any>(), sent.captured.historyGame)
        assertEquals(emptyMap<String, Any>(), sent.captured.historyGameExpansion)
        coVerify(exactly = 1) { syncState.markSynced(snapshot.fingerprint()) }
    }

    @Test
    fun uploadFails_doesNotMarkSynced() = runTest {
        coEvery { api.uploadAll(any()) } returns RequestResult.Error(Throwable("offline"))

        assertFalse(useCase())
        coVerify(exactly = 0) { syncState.markSynced(any()) }
    }
}
