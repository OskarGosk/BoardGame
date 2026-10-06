package com.goskar.boardgame.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class LocalSnapshotTest {

    private fun game(id: String, plays: Int = 0) = Game(
        name = id, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = plays, id = id,
    )

    private fun player(id: String, selected: Boolean = false) =
        Player(name = id, games = 0, winRatio = 0, description = "", selected = selected, id = id)

    private fun history(id: String, scores: Map<String, Int>? = null) = HistoryGame(
        gameName = "g", winner = "w", gameData = LocalDate.of(2024, 1, 1), listOfPlayer = listOf("a", "b"),
        description = "", playerScores = scores, id = id,
    )

    private fun snapshot(
        games: List<Game> = emptyList(),
        players: List<Player> = emptyList(),
        history: List<HistoryGame> = emptyList(),
        expansions: List<HistoryGameExpansion> = emptyList(),
    ) = LocalSnapshot(games, players, history, expansions)

    @Test
    fun isEmpty_trueOnlyWhenEverythingIsEmpty() {
        assertTrue(snapshot().isEmpty)
        assertFalse(snapshot(games = listOf(game("g1"))).isEmpty)
        assertFalse(snapshot(history = listOf(history("h1"))).isEmpty)
    }

    @Test
    fun fingerprint_ignoresRowOrder() {
        val a = snapshot(games = listOf(game("g1"), game("g2")), players = listOf(player("p1"), player("p2")))
        val b = snapshot(games = listOf(game("g2"), game("g1")), players = listOf(player("p2"), player("p1")))

        assertEquals(a.fingerprint(), b.fingerprint())
    }

    @Test
    fun fingerprint_ignoresScoreMapOrder() {
        val a = snapshot(history = listOf(history("h1", linkedMapOf("a" to 1, "b" to 2))))
        val b = snapshot(history = listOf(history("h1", linkedMapOf("b" to 2, "a" to 1))))

        assertEquals(a.fingerprint(), b.fingerprint())
    }

    @Test
    fun fingerprint_ignoresPlayerSelectionState() {
        val a = snapshot(players = listOf(player("p1", selected = false)))
        val b = snapshot(players = listOf(player("p1", selected = true)))

        assertEquals(a.fingerprint(), b.fingerprint())
    }

    @Test
    fun fingerprint_changesWhenDataChanges() {
        val base = snapshot(games = listOf(game("g1", plays = 1)))

        assertNotEquals(base.fingerprint(), snapshot(games = listOf(game("g1", plays = 2))).fingerprint())
        assertNotEquals(base.fingerprint(), snapshot(games = listOf(game("g1", plays = 1)), history = listOf(history("h1"))).fingerprint())
    }
}
