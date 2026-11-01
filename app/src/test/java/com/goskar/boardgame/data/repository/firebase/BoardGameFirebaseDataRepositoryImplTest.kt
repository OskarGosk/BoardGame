package com.goskar.boardgame.data.repository.firebase

import com.goskar.boardgame.data.db.UserSessionDao
import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.rest.ApiFirebaseData
import com.goskar.boardgame.data.rest.RequestResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BoardGameFirebaseDataRepositoryImplTest {

    private lateinit var api: ApiFirebaseData
    private lateinit var userSession: UserSessionDao
    private lateinit var repository: BoardGameFirebaseDataRepositoryImpl

    private fun game(id: String) = Game(
        name = "Root", expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 1, id = id,
    )

    @Before
    fun setUp() {
        api = mockk()
        userSession = mockk()
        every { userSession.getUserUID() } returns "uid1"
        repository = BoardGameFirebaseDataRepositoryImpl(api, userSession)
    }

    @Test
    fun getUserData_readsTheSignedInUsersNode() = runTest {
        val data = CloudUserData(game = mapOf("g1" to game(id = "g1")))
        coEvery { api.getUserData("uid1") } returns Response.success(data)

        assertEquals(RequestResult.Success(data), repository.getUserData())
    }

    @Test
    fun getUserData_missingNode_isEmptyDataNotAnError() = runTest {
        coEvery { api.getUserData("uid1") } returns Response.success(null)

        assertEquals(RequestResult.Success(CloudUserData()), repository.getUserData())
    }

    @Test
    fun guestSession_failsWithoutCallingFirebase() = runTest {
        every { userSession.getUserUID() } returns "guest"

        assertTrue(repository.getUserData() is RequestResult.Error)
        assertTrue(repository.uploadAll(CloudUserData()) is RequestResult.Error)
        coVerify(exactly = 0) { api.getUserData(any()) }
        coVerify(exactly = 0) { api.updateUserData(any(), any()) }
    }

    @Test
    fun missingOrBlankUid_neverWritesToUsersRoot() = runTest {
        every { userSession.getUserUID() } returnsMany listOf(null, " ")

        assertTrue(repository.uploadAll(CloudUserData()) is RequestResult.Error)
        assertTrue(repository.uploadAll(CloudUserData()) is RequestResult.Error)
        coVerify(exactly = 0) { api.updateUserData(any(), any()) }
    }

    @Test
    fun uploadAll_rejectedByFirebase_returnsError() = runTest {
        coEvery { api.updateUserData("uid1", any()) } returns
            Response.error(401, "Permission denied".toResponseBody())

        assertTrue(repository.uploadAll(CloudUserData()) is RequestResult.Error)
    }

    @Test
    fun uploadAll_accepted_returnsSuccess() = runTest {
        val data = CloudUserData(game = mapOf("g1" to game(id = "g1")))
        coEvery { api.updateUserData("uid1", data) } returns Response.success(null)

        assertEquals(RequestResult.Success(true), repository.uploadAll(data))
    }

    @Test
    fun hasUserData_dependsOnKeysUnderUserNode() = runTest {
        coEvery { api.getUserDataKeys("uid1") } returnsMany
            listOf(Response.success(null), Response.success(emptyMap()), Response.success(mapOf("game" to true)))

        assertEquals(RequestResult.Success(false), repository.hasUserData())
        assertEquals(RequestResult.Success(false), repository.hasUserData())
        assertEquals(RequestResult.Success(true), repository.hasUserData())
    }

    @Test
    fun getLegacyData_readsOldPathsAsTheyAre() = runTest {
        val games = mapOf("g1" to game(id = "g1"))
        coEvery { api.getLegacyGames("uid1") } returns Response.success(games)
        coEvery { api.getLegacyPlayers("uid1") } returns Response.success(null)
        coEvery { api.getLegacyHistoryGames("uid1") } returns Response.success(null)
        coEvery { api.getLegacyHistoryGameExpansions("uid1") } returns Response.success(null)

        assertEquals(RequestResult.Success(CloudUserData(game = games)), repository.getLegacyData())
    }

    @Test
    fun readRejectedByFirebase_isAnErrorNotEmptyData() = runTest {
        coEvery { api.getUserData("uid1") } returns Response.error(401, "Permission denied".toResponseBody())
        coEvery { api.getUserDataKeys("uid1") } returns Response.error(401, "Permission denied".toResponseBody())

        assertTrue(repository.getUserData() is RequestResult.Error)
        assertTrue(repository.hasUserData() is RequestResult.Error)
    }
}
