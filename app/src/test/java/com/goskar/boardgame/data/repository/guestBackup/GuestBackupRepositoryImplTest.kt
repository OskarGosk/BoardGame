package com.goskar.boardgame.data.repository.guestBackup

import android.content.Context
import android.provider.Settings
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.GuestBackupDto
import com.goskar.boardgame.data.models.GuestBackupMeta
import com.goskar.boardgame.data.rest.ApiGuestBackup
import com.goskar.boardgame.data.rest.RequestResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class GuestBackupRepositoryImplTest {

    private lateinit var api: ApiGuestBackup
    private lateinit var repository: GuestBackupRepositoryImpl

    @Before
    fun setUp() {
        val tokenTask = mockk<Task<GetTokenResult>> {
            every { isComplete } returns true
            every { exception } returns null
            every { isCanceled } returns false
            every { result } returns mockk { every { token } returns "token" }
        }
        val user = mockk<FirebaseUser> { every { getIdToken(false) } returns tokenTask }
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockk { every { currentUser } returns user }
        mockkStatic(Settings.Secure::class)
        every { Settings.Secure.getString(any(), Settings.Secure.ANDROID_ID) } returns "android-id"

        api = mockk()
        repository = GuestBackupRepositoryImpl(mockk(relaxed = true), api)
    }

    @After
    fun tearDown() {
        unmockkStatic(FirebaseAuth::class)
        unmockkStatic(Settings.Secure::class)
    }

    @Test
    fun getBackupInfo_noBackupOnServer_isNoneNotAnError() = runTest {
        coEvery { api.getMeta(any(), "token") } returns Response.success(null)

        assertEquals(RequestResult.Success(GuestBackupInfo.None), repository.getBackupInfo())
    }

    @Test
    fun getBackupInfo_backupOnServer_isAvailable() = runTest {
        val meta = GuestBackupMeta(updatedAt = 1L, gameCount = 2)
        coEvery { api.getMeta(any(), "token") } returns Response.success(meta)

        assertEquals(RequestResult.Success(GuestBackupInfo.Available(meta)), repository.getBackupInfo())
    }

    @Test
    fun getBackupInfo_rejectedByFirebase_isAnError() = runTest {
        coEvery { api.getMeta(any(), "token") } returns Response.error(401, "Permission denied".toResponseBody())

        assertTrue(repository.getBackupInfo() is RequestResult.Error)
    }

    @Test
    fun download_noBackupOnServer_isAnError() = runTest {
        coEvery { api.download(any(), "token") } returns Response.success(null)

        assertTrue(repository.download() is RequestResult.Error)
    }

    @Test
    fun download_backupOnServer_restoresIdsFromKeys() = runTest {
        val game = Game(
            name = "Root", expansion = false, cooperate = false, baseGame = "",
            minPlayer = "1", maxPlayer = "4", games = 1, id = "",
        )
        coEvery { api.download(any(), "token") } returns Response.success(GuestBackupDto(game = mapOf("g1" to game)))

        val result = repository.download()

        assertEquals(listOf("g1"), (result as RequestResult.Success).data.games.map { it.id })
    }
}
