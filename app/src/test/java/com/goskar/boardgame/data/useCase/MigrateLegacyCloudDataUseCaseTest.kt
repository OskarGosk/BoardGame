package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.rest.RequestResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MigrateLegacyCloudDataUseCaseTest {

    private lateinit var api: BoardGameFirebaseDataRepository
    private lateinit var useCase: MigrateLegacyCloudDataUseCase

    private val legacy = CloudUserData(
        game = mapOf(
            "g1" to Game(
                name = "Root", expansion = false, cooperate = false, baseGame = "",
                minPlayer = "1", maxPlayer = "4", games = 1, id = "g1",
            )
        ),
    )

    @Before
    fun setUp() {
        api = mockk()
        useCase = MigrateLegacyCloudDataUseCase(api)
    }

    @Test
    fun userNodeAlreadyExists_doesNotTouchLegacyData() = runTest {
        coEvery { api.hasUserData() } returns RequestResult.Success(true)

        assertTrue(useCase())
        coVerify(exactly = 0) { api.getLegacyData() }
        coVerify(exactly = 0) { api.uploadAll(any()) }
    }

    @Test
    fun noUserNode_copiesLegacyDataInOneUpload() = runTest {
        coEvery { api.hasUserData() } returns RequestResult.Success(false)
        coEvery { api.getLegacyData() } returns RequestResult.Success(legacy)
        coEvery { api.uploadAll(legacy) } returns RequestResult.Success(true)

        assertTrue(useCase())
        coVerify(exactly = 1) { api.uploadAll(legacy) }
    }

    @Test
    fun newAccountWithoutAnyData_succeedsWithoutUpload() = runTest {
        coEvery { api.hasUserData() } returns RequestResult.Success(false)
        coEvery { api.getLegacyData() } returns RequestResult.Success(CloudUserData())

        assertTrue(useCase())
        coVerify(exactly = 0) { api.uploadAll(any()) }
    }

    @Test
    fun legacyNodesWithEmptyMaps_countAsNoData() = runTest {
        coEvery { api.hasUserData() } returns RequestResult.Success(false)
        coEvery { api.getLegacyData() } returns
            RequestResult.Success(CloudUserData(game = emptyMap(), player = emptyMap()))

        assertTrue(useCase())
        coVerify(exactly = 0) { api.uploadAll(any()) }
    }

    @Test
    fun checkFails_stopsBeforeReadingLegacyData() = runTest {
        coEvery { api.hasUserData() } returns RequestResult.Error(Throwable("offline"))

        assertFalse(useCase())
        coVerify(exactly = 0) { api.getLegacyData() }
        coVerify(exactly = 0) { api.uploadAll(any()) }
    }

    @Test
    fun legacyReadFails_neverWritesPartialData() = runTest {
        coEvery { api.hasUserData() } returns RequestResult.Success(false)
        coEvery { api.getLegacyData() } returns RequestResult.Error(Throwable("offline"))

        assertFalse(useCase())
        coVerify(exactly = 0) { api.uploadAll(any()) }
    }

    @Test
    fun uploadFails_returnsFalse() = runTest {
        coEvery { api.hasUserData() } returns RequestResult.Success(false)
        coEvery { api.getLegacyData() } returns RequestResult.Success(legacy)
        coEvery { api.uploadAll(legacy) } returns RequestResult.Error(Throwable("denied"))

        assertFalse(useCase())
    }
}
