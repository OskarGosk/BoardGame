package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.DeletedRecord
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.GameMode
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.models.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CloudMergeTest {

    private fun game(id: String, name: String = id, updatedAt: Long = 0L, games: Int = 0) = Game(
        name = name, expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = games, id = id, updatedAt = updatedAt,
    )

    private fun player(id: String, name: String = id, updatedAt: Long = 0L, games: Int = 0, wins: Int = 0) =
        Player(name = name, games = games, winRatio = wins, description = "", selected = false, id = id, updatedAt = updatedAt)

    private fun session(
        id: String,
        game: String = "Root",
        players: List<String> = emptyList(),
        winner: String = "",
        updatedAt: Long = 0L,
        mode: GameMode? = null,
        teams: Map<String, Int>? = null,
        winningTeam: Int? = null,
        description: String = "",
    ) = HistoryGame(
        gameName = game, winner = winner, gameData = LocalDate.of(2026, 1, 1), listOfPlayer = players,
        description = description, id = id, updatedAt = updatedAt, mode = mode, teams = teams, winningTeam = winningTeam,
    )

    private fun expansion(id: String, sessionId: String, expansionId: String) =
        HistoryGameExpansion(id = id, historyGameId = sessionId, expansionName = expansionId, expansionId = expansionId)

    private fun data(
        games: List<Game> = emptyList(),
        players: List<Player> = emptyList(),
        history: List<HistoryGame> = emptyList(),
        expansions: List<HistoryGameExpansion> = emptyList(),
        deleted: List<DeletedRecord> = emptyList(),
    ) = SyncData(LocalSnapshot(games, players, history, expansions), deleted)

    private fun merge(local: SyncData, remote: SyncData) = CloudMerge.merge(local, remote).snapshot

    @Test
    fun recordsAddedOnEitherDevice_areAllKept() {
        val merged = merge(
            data(games = listOf(game("g1")), history = listOf(session("s1"))),
            data(games = listOf(game("g2")), history = listOf(session("s2"))),
        )

        assertEquals(setOf("g1", "g2"), merged.games.map { it.id }.toSet())
        assertEquals(setOf("s1", "s2"), merged.history.map { it.id }.toSet())
    }

    @Test
    fun sameRecordEditedOnBothDevices_newerEditWins() {
        val merged = merge(
            data(history = listOf(session("s1", description = "local", updatedAt = 200))),
            data(history = listOf(session("s1", description = "remote", updatedAt = 300))),
        )

        assertEquals("remote", merged.history.single().description)
    }

    @Test
    fun equalTimestamps_keepLocalCopy() {
        val merged = merge(
            data(games = listOf(game("g1", name = "Local name"))),
            data(games = listOf(game("g1", name = "Remote name"))),
        )

        assertEquals("Local name", merged.games.single().name)
    }

    @Test
    fun deletionOnOneDevice_removesRecordKeptByTheOther() {
        val merged = CloudMerge.merge(
            data(players = listOf(player("p1", updatedAt = 100))),
            data(deleted = listOf(DeletedRecord("p1", DeletedRecord.PLAYER, deletedAt = 500))),
        )

        assertTrue(merged.snapshot.players.isEmpty())
        assertEquals(listOf("p1"), merged.deleted.map { it.id })
    }

    @Test
    fun legacyRecordWithoutTimestamp_isRemovedByAnyDeletion() {
        val merged = merge(
            data(games = listOf(game("g1"))),
            data(deleted = listOf(DeletedRecord("g1", DeletedRecord.GAME, deletedAt = 1))),
        )

        assertTrue(merged.games.isEmpty())
    }

    @Test
    fun editMadeAfterDeletion_bringsRecordBack() {
        val merged = merge(
            data(games = listOf(game("g1", updatedAt = 900))),
            data(deleted = listOf(DeletedRecord("g1", DeletedRecord.GAME, deletedAt = 500))),
        )

        assertEquals(listOf("g1"), merged.games.map { it.id })
    }

    @Test
    fun deletionRecordedTwice_keepsTheLatestTime() {
        val merged = CloudMerge.merge(
            data(deleted = listOf(DeletedRecord("g1", DeletedRecord.GAME, deletedAt = 100))),
            data(deleted = listOf(DeletedRecord("g1", DeletedRecord.GAME, deletedAt = 700))),
        )

        assertEquals(listOf(700L), merged.deleted.map { it.deletedAt })
    }

    @Test
    fun sessionExpansions_followTheCopyThatWon() {
        val merged = merge(
            data(
                history = listOf(session("s1", updatedAt = 100)),
                expansions = listOf(expansion("e-old", "s1", "x1")),
            ),
            data(
                history = listOf(session("s1", updatedAt = 200)),
                expansions = listOf(expansion("e-new", "s1", "x2")),
            ),
        )

        assertEquals(listOf("e-new"), merged.historyExpansions.map { it.id })
    }

    @Test
    fun deletedSession_dropsItsExpansions() {
        val merged = merge(
            data(
                history = listOf(session("s1", updatedAt = 100)),
                expansions = listOf(expansion("e1", "s1", "x1")),
            ),
            data(deleted = listOf(DeletedRecord("s1", DeletedRecord.HISTORY_GAME, deletedAt = 200))),
        )

        assertTrue(merged.history.isEmpty())
        assertTrue(merged.historyExpansions.isEmpty())
    }

    @Test
    fun gameCounter_countsSessionsByNameAndExpansionPlays() {
        val merged = merge(
            data(
                games = listOf(game("root", name = "Root", games = 99), game("x1", name = "Riverfolk", games = 99)),
                history = listOf(session("s1", game = "Root"), session("s2", game = "Root")),
                expansions = listOf(expansion("e1", "s1", "x1")),
            ),
            data(history = listOf(session("s3", game = "Root"))),
        )

        assertEquals(mapOf("Root" to 3, "Riverfolk" to 1), merged.games.associate { it.name to it.games })
    }

    @Test
    fun playerCounters_countSessionsAndWinsAcrossDevices() {
        val merged = merge(
            data(
                players = listOf(player("p1", name = "Ala", games = 1, wins = 1)),
                history = listOf(session("s1", players = listOf("Ala", "Ola"), winner = "Ala")),
            ),
            data(history = listOf(session("s2", players = listOf("Ala", "Ola"), winner = "Ola"))),
        )

        val ala = merged.players.single()
        assertEquals(2, ala.games)
        assertEquals(1, ala.winRatio)
    }

    @Test
    fun playerCounters_includeCoopAndTeamWins() {
        val history = listOf(
            session("coop", players = listOf("Ala", "Computer"), winner = "Players"),
            session(
                "team", players = listOf("Ala", "Ola"), mode = GameMode.TEAM,
                teams = mapOf("Ala" to 1, "Ola" to 2), winningTeam = 1,
            ),
            session("other", players = listOf("Ola"), winner = "Players"),
        )

        val ala = CloudMerge.recountPlayers(listOf(player("p1", name = "Ala")), history).single()

        assertEquals(2, ala.games)
        assertEquals(2, ala.winRatio)
    }

    @Test
    fun recountedCounters_doNotChangeUpdatedAt() {
        val merged = merge(
            data(games = listOf(game("g1", name = "Root", updatedAt = 42, games = 7))),
            data(),
        )

        assertEquals(42L, merged.games.single().updatedAt)
        assertEquals(0, merged.games.single().games)
    }

    @Test
    fun cloudDataRoundTrip_keepsIdsFromKeys() {
        val original = data(
            games = listOf(game("g1")),
            players = listOf(player("p1")),
            history = listOf(session("s1", players = listOf("p1"))),
            expansions = listOf(expansion("e1", "s1", "g1")),
            deleted = listOf(DeletedRecord("old", DeletedRecord.GAME, 5)),
        )

        assertTrue(original.toCloudUserData().toSyncData().sameAs(original))
    }

    @Test
    fun cloudDataWithoutAnyNode_isEmpty() {
        val empty = CloudUserData().toSyncData()

        assertTrue(empty.snapshot.isEmpty)
        assertTrue(empty.deleted.isEmpty())
    }

    @Test
    fun sameAs_ignoresOrderButNotDeletions() {
        val a = data(games = listOf(game("g1"), game("g2")))
        val b = data(games = listOf(game("g2"), game("g1")))
        val c = b.copy(deleted = listOf(DeletedRecord("g3", DeletedRecord.GAME, 1)))

        assertTrue(a.sameAs(b))
        assertFalse(a.sameAs(c))
    }
}
