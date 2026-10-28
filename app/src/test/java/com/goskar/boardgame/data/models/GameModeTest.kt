package com.goskar.boardgame.data.models

import com.goskar.boardgame.data.db.Converters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GameModeTest {

    private fun game(coop: Boolean, modes: List<GameMode>?) = Game(
        name = "G", expansion = false, cooperate = coop, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, modes = modes,
    )

    private fun history(
        winner: String,
        mode: GameMode? = null,
        teams: Map<String, Int>? = null,
        winningTeam: Int? = null,
    ) = HistoryGame(
        gameName = "G", winner = winner, gameData = LocalDate.of(2026, 1, 1),
        listOfPlayer = listOf("Alex", "Bob"), description = "",
        mode = mode, teams = teams, winningTeam = winningTeam,
    )

    @Test
    fun playModes_legacyGame_followsTheCoopFlag() {
        assertEquals(listOf(GameMode.COOPERATIVE), game(coop = true, modes = null).playModes())
        assertEquals(listOf(GameMode.COMPETITIVE), game(coop = false, modes = null).playModes())
    }

    @Test
    fun playModes_emptyOrDuplicatedList_isNormalised() {
        assertEquals(listOf(GameMode.COMPETITIVE), game(coop = false, modes = emptyList()).playModes())
        assertEquals(
            listOf(GameMode.TEAM, GameMode.COMPETITIVE),
            game(coop = false, modes = listOf(GameMode.TEAM, GameMode.COMPETITIVE, GameMode.TEAM)).playModes(),
        )
    }

    @Test
    fun playMode_legacySession_isInferredFromTheWinner() {
        assertEquals(GameMode.COOPERATIVE, history(winner = CoopWinner.PLAYERS).playMode())
        assertEquals(GameMode.COOPERATIVE, history(winner = CoopWinner.COMPUTER).playMode())
        assertEquals(GameMode.COMPETITIVE, history(winner = "Alex").playMode())
    }

    @Test
    fun isWinner_competitive_matchesTheNameIgnoringCase() {
        assertTrue(history(winner = "alex").isWinner("Alex"))
        assertFalse(history(winner = "Alex").isWinner("Bob"))
    }

    @Test
    fun isWinner_coop_isEveryoneOrNobody() {
        assertTrue(history(winner = CoopWinner.PLAYERS).isWinner("Bob"))
        assertFalse(history(winner = CoopWinner.COMPUTER).isWinner("Bob"))
    }

    @Test
    fun isWinner_team_isEveryMemberOfTheWinningTeam() {
        val session = history(
            winner = "Alex, Cara", mode = GameMode.TEAM,
            teams = mapOf("Alex" to 1, "Bob" to 2, "Cara" to 1), winningTeam = 1,
        )
        assertTrue(session.isWinner("Cara"))
        assertFalse(session.isWinner("Bob"))
        assertFalse(session.isWinner("Nobody"))
    }

    @Test
    fun modesConverter_roundTrips_andSkipsUnknownNames() {
        val converters = Converters()
        val modes = listOf(GameMode.COMPETITIVE, GameMode.TEAM)

        assertEquals(modes, converters.fromModesString(converters.fromModesList(modes)))
        assertEquals(listOf(GameMode.COOPERATIVE), converters.fromModesString("COOPERATIVE,SOLO"))
        assertNull(converters.fromModesString(null))
    }
}
