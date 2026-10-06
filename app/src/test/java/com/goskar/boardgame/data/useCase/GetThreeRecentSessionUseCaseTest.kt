package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetThreeRecentSessionUseCaseTest {

    private lateinit var getAllGame: GetAllGameUseCase
    private lateinit var getAllHistory: GetAllHistoryGameUseCase
    private lateinit var useCase: GetThreeRecentSessionUseCase

    private fun game(name: String, uri: String? = null, uriFromBgg: String? = null) = Game(
        name = name, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, uri = uri, uriFromBgg = uriFromBgg, id = name,
    )

    private fun history(gameName: String, day: Int, baseGameId: String? = null) = HistoryGame(
        gameName = gameName, winner = "Alex M.", gameData = LocalDate.of(2024, 1, day),
        listOfPlayer = listOf("Alex M.", "Bob"), description = "", baseGameId = baseGameId,
    )

    @Before
    fun setUp() {
        getAllGame = mockk()
        getAllHistory = mockk()
        useCase = GetThreeRecentSessionUseCase(getAllGame, getAllHistory)
    }

    @Test
    fun returnsThreeNewestSessionsFirst() = runTest {
        coEvery { getAllGame() } returns emptyList()
        coEvery { getAllHistory() } returns (1..5).map { history("G$it", day = it) }

        assertEquals(listOf("G5", "G4", "G3"), useCase().map { it.gameName })
    }

    @Test
    fun cover_isFoundByGameName_evenWhenHistoryHasNoBaseGameId() = runTest {
        // HistoryGame.baseGameId is the parent of an expansion, so it is null for a base game.
        coEvery { getAllGame() } returns listOf(game("Root", uriFromBgg = "https://img/root.jpg"))
        coEvery { getAllHistory() } returns listOf(history("Root", day = 1, baseGameId = null))

        assertEquals("https://img/root.jpg", useCase().single().uri)
    }

    @Test
    fun cover_prefersBggImage_andIgnoresBlankValues() = runTest {
        coEvery { getAllGame() } returns listOf(
            game("Root", uri = "content://own/root", uriFromBgg = "https://img/root.jpg"),
            game("Wingspan", uri = "content://own/wingspan", uriFromBgg = ""),
        )
        coEvery { getAllHistory() } returns listOf(history("Root", day = 2), history("Wingspan", day = 1))

        assertEquals(
            listOf("https://img/root.jpg", "content://own/wingspan"),
            useCase().map { it.uri },
        )
    }

    @Test
    fun unknownGame_hasEmptyCover() = runTest {
        coEvery { getAllGame() } returns emptyList()
        coEvery { getAllHistory() } returns listOf(history("Ghost", day = 1))

        assertEquals("", useCase().single().uri)
    }

    @Test
    fun session_carriesWinnerYearAndPlayerInitials() = runTest {
        coEvery { getAllGame() } returns emptyList()
        coEvery { getAllHistory() } returns listOf(history("Root", day = 1))

        val session = useCase().single()

        assertEquals("Winner: Alex M.", session.winner)
        assertEquals("2024", session.date)
        assertEquals(listOf("AM", "B"), session.playersInitials)
    }
}
