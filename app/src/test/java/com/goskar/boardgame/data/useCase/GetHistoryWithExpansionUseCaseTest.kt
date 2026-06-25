package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import io.mockk.coEvery
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GetHistoryWithExpansionUseCaseTest {

    private val chess = createGame(name = "Chess")
    private val azul = createGame(name = "Azul")

    private fun createGame(
        name: String,
        gameData: LocalDate = LocalDate.parse("2025-01-01"),
        listOfPlayer: List<String> = listOf("Oskar", "Alice"),
        winner: String = listOfPlayer.first(),
        description: String = "",
        id: String = name + winner,
    ) = HistoryGame(
        gameName = name,
        winner = winner,
        gameData = gameData,
        listOfPlayer = listOfPlayer,
        description = description,
        id = id
    )

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var repo: GamesHistoryDbRepository
    private lateinit var useCase: GetHistoryWithExpansionUseCase

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        useCase = GetHistoryWithExpansionUseCase(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // Success path
    // -------------------------------------------------------------------------

    @Test
    fun invoke_bothRepositoriesSucceed_returnsSuccessWithJoinedData() = runTest(testDispatcher) {
        val game1 = chess.copy(id = "id-1")
        val game2 = azul.copy(id = "id-2")
        val exp1 = HistoryGameExpansion(id = "e1", historyGameId = "id-1", expansionName = "Exp 1", expansionId = "exp-1")
        val exp2 = HistoryGameExpansion(id = "e2", historyGameId = "id-1", expansionName = "Exp 2", expansionId = "exp-2")
        val exp3 = HistoryGameExpansion(id = "e3", historyGameId = "id-2", expansionName = "Exp 3", expansionId = "exp-3")

        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(listOf(game1, game2))
        coEvery { repo.getAllHistoryGameExpansion() } returns RequestResult.Success(listOf(exp1, exp2, exp3))

        val result = useCase.invoke()

        assertTrue(result is RequestResult.Success)
        val data = (result as RequestResult.Success).data
        assertEquals(2, data.size)

        assertEquals(game1, data[0].history)
        assertEquals(2, data[0].expansion?.size)
        assertTrue(data[0].expansion?.contains(exp1) == true)
        assertTrue(data[0].expansion?.contains(exp2) == true)

        assertEquals(game2, data[1].history)
        assertEquals(1, data[1].expansion?.size)
        assertEquals(exp3, data[1].expansion?.get(0))
    }

    @Test
    fun invoke_gameHasNoMatchingExpansions_expansionListIsEmpty() = runTest(testDispatcher) {
        val game1 = chess.copy(id = "id-1")

        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(listOf(game1))
        coEvery { repo.getAllHistoryGameExpansion() } returns RequestResult.Success(emptyList())

        val result = useCase.invoke()
        assertTrue(result is RequestResult.Success)
        val data = (result as RequestResult.Success).data
        assertTrue(data[0].expansion?.isEmpty() == true)
    }

    @Test
    fun invoke_emptyHistoryList_returnsSuccessWithEmptyData() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(emptyList())
        coEvery { repo.getAllHistoryGameExpansion() } returns RequestResult.Success(emptyList())

        val result = useCase.invoke()
        assertTrue(result is RequestResult.Success)
        val data = (result as RequestResult.Success).data
        assertTrue(data.isEmpty())
    }

    // -------------------------------------------------------------------------
    // Error paths
    // -------------------------------------------------------------------------

    @Test
    fun invoke_historyRepositoryFails_returnsHistoryError() = runTest(testDispatcher) {
        coEvery { repo.getAllHistoryGame() } returns RequestResult.Error(Throwable("db error"))
        coEvery { repo.getAllHistoryGameExpansion() } returns RequestResult.Success(emptyList())

        val result = useCase.invoke()
        assertTrue(result is RequestResult.Error)
    }

    @Test
    fun invoke_expansionRepositoryFails_returnsExpansionError() = runTest(testDispatcher) {
        val game1 = chess.copy(id = "id-1")

        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(listOf(game1))
        coEvery { repo.getAllHistoryGameExpansion() } returns RequestResult.Error(Throwable("exp error"))

        val result = useCase.invoke()
        assertTrue(result is RequestResult.Error)
    }

    // -------------------------------------------------------------------------
    // Data isolation
    // -------------------------------------------------------------------------

    @Test
    fun invoke_expansionsForDifferentGames_noCrossContamination() = runTest(testDispatcher) {
        val game1 = chess.copy(id = "id-1")
        val game2 = azul.copy(id = "id-2")
        val game3 = azul.copy(id = "id-3", gameName = "Wingspan")

        val exp1 = HistoryGameExpansion(id = "e1", historyGameId = "id-1", expansionName = "Exp 1", expansionId = "exp-1")
        val exp2 = HistoryGameExpansion(id = "e2", historyGameId = "id-1", expansionName = "Exp 2", expansionId = "exp-2")
        val exp3 = HistoryGameExpansion(id = "e3", historyGameId = "id-3", expansionName = "Exp 3", expansionId = "exp-3")

        coEvery { repo.getAllHistoryGame() } returns RequestResult.Success(listOf(game1, game2, game3))
        coEvery { repo.getAllHistoryGameExpansion() } returns RequestResult.Success(listOf(exp1, exp2, exp3))

        val result = useCase.invoke()
        assertTrue(result is RequestResult.Success)
        val data = (result as RequestResult.Success).data
        assertEquals(3, data.size)

        assertEquals(game1, data[0].history)
        assertEquals(2, data[0].expansion?.size)
        assertEquals(0, data[1].expansion?.size)
        assertEquals(1, data[2].expansion?.size)
    }
}
