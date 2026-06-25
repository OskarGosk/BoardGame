package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.repository.dbRepository.GameDbRepository
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.rest.RequestResult
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClearDbUseCaseTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var gameRepo: GameDbRepository
    private lateinit var playerRepo: PlayerDbRepository
    private lateinit var historyRepo: GamesHistoryDbRepository
    private lateinit var useCase: ClearDbUseCase

    private val success = RequestResult.Success(true)
    private val error = RequestResult.Error(Throwable("db error"))

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        gameRepo = mockk()
        playerRepo = mockk()
        historyRepo = mockk()
        useCase = ClearDbUseCase(gameRepo, playerRepo, historyRepo)

        // Default: everything succeeds. Individual tests override one step to fail.
        coEvery { gameRepo.deleteAllGame() } returns success
        coEvery { playerRepo.deleteAllPlayer() } returns success
        coEvery { historyRepo.deleteAllHistory() } returns success
        coEvery { historyRepo.deleteAllHistoryExpansion() } returns success
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun invoke_allStepsSucceed_returnsTrueAndCallsEveryStep() = runTest {
        val result = useCase.invoke()

        assertEquals(true, result)
        coVerify(exactly = 1) { gameRepo.deleteAllGame() }
        coVerify(exactly = 1) { playerRepo.deleteAllPlayer() }
        coVerify(exactly = 1) { historyRepo.deleteAllHistory() }
        coVerify(exactly = 1) { historyRepo.deleteAllHistoryExpansion() }
    }

    @Test
    fun invoke_firstStepFails_returnsFalseAndShortCircuits() = runTest {
        coEvery { gameRepo.deleteAllGame() } returns error

        val result = useCase.invoke()

        assertEquals(false, result)
        coVerify(exactly = 0) { playerRepo.deleteAllPlayer() }
        coVerify(exactly = 0) { historyRepo.deleteAllHistory() }
        coVerify(exactly = 0) { historyRepo.deleteAllHistoryExpansion() }
    }

    @Test
    fun invoke_secondStepFails_returnsFalseAndShortCircuits() = runTest {
        coEvery { playerRepo.deleteAllPlayer() } returns error

        val result = useCase.invoke()

        assertEquals(false, result)
        coVerify(exactly = 1) { gameRepo.deleteAllGame() }
        coVerify(exactly = 0) { historyRepo.deleteAllHistory() }
        coVerify(exactly = 0) { historyRepo.deleteAllHistoryExpansion() }
    }

    @Test
    fun invoke_thirdStepFails_returnsFalseAndShortCircuits() = runTest {
        coEvery { historyRepo.deleteAllHistory() } returns error

        val result = useCase.invoke()

        assertEquals(false, result)
        coVerify(exactly = 1) { playerRepo.deleteAllPlayer() }
        coVerify(exactly = 0) { historyRepo.deleteAllHistoryExpansion() }
    }

    @Test
    fun invoke_fourthStepFails_returnsFalse() = runTest {
        coEvery { historyRepo.deleteAllHistoryExpansion() } returns error

        val result = useCase.invoke()

        assertEquals(false, result)
        coVerify(exactly = 1) { historyRepo.deleteAllHistory() }
        coVerify(exactly = 1) { historyRepo.deleteAllHistoryExpansion() }
    }
}
