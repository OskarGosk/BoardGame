package com.goskar.boardgame.data.useCase

import com.goskar.boardgame.data.models.CloudUserData
import com.goskar.boardgame.data.models.DeletedRecord
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameExpansion
import com.goskar.boardgame.data.models.LocalSnapshot
import com.goskar.boardgame.data.models.Player
import com.goskar.boardgame.data.models.isWinner
import com.goskar.boardgame.utils.convertHistoryGameListToDto
import com.goskar.boardgame.utils.convertHistoryGameListToFirebase

data class SyncData(
    val snapshot: LocalSnapshot,
    val deleted: List<DeletedRecord>,
) {
    fun sameAs(other: SyncData): Boolean =
        snapshot.fingerprint() == other.snapshot.fingerprint() && deleted.toSet() == other.deleted.toSet()
}

fun CloudUserData.toSyncData() = SyncData(
    snapshot = LocalSnapshot(
        games = game.orEmpty().map { (id, game) -> game.copy(id = id) },
        players = player.orEmpty().map { (id, player) -> player.copy(id = id) },
        history = convertHistoryGameListToDto(historyGame.orEmpty().map { (id, history) -> history.copy(id = id) }),
        historyExpansions = historyGameExpansion.orEmpty().map { (id, expansion) -> expansion.copy(id = id) },
    ),
    deleted = deleted.orEmpty().map { (id, record) -> record.copy(id = id) },
)

fun SyncData.toCloudUserData() = CloudUserData(
    game = snapshot.games.associateBy { it.id },
    player = snapshot.players.associateBy { it.id },
    historyGame = convertHistoryGameListToFirebase(snapshot.history).associateBy { it.id },
    historyGameExpansion = snapshot.historyExpansions.associateBy { it.id },
    deleted = deleted.associateBy { it.id },
)

/**
 * Merges two copies of the library record by record: the newer `updatedAt` wins (local on a tie, so
 * edits made before updating the app are kept), a deletion wins over versions saved before it, and a
 * session keeps the expansions of whichever copy won. Play and win counters are then recounted from
 * the merged sessions, because increments made on two devices cannot be merged.
 */
object CloudMerge {

    fun merge(local: SyncData, remote: SyncData): SyncData {
        val deleted = (local.deleted + remote.deleted)
            .groupBy { it.id }
            .map { (_, records) -> records.maxBy { it.deletedAt } }
        val deletedAt = deleted.associate { it.id to it.deletedAt }

        val games = pickNewer(local.snapshot.games, remote.snapshot.games, deletedAt, Game::id, Game::updatedAt)
        val players = pickNewer(local.snapshot.players, remote.snapshot.players, deletedAt, Player::id, Player::updatedAt)
        val history = pickNewer(
            local.snapshot.history, remote.snapshot.history, deletedAt, HistoryGame::id, HistoryGame::updatedAt,
        )

        val localExpansions = local.snapshot.historyExpansions.groupBy { it.historyGameId }
        val remoteExpansions = remote.snapshot.historyExpansions.groupBy { it.historyGameId }
        val expansions = history.flatMap { picked ->
            (if (picked.fromLocal) localExpansions else remoteExpansions)[picked.value.id].orEmpty()
        }

        val sessions = history.map { it.value }
        return SyncData(
            snapshot = LocalSnapshot(
                games = recountGames(games.map { it.value }, sessions, expansions),
                players = recountPlayers(players.map { it.value }, sessions),
                history = sessions,
                historyExpansions = expansions,
            ),
            deleted = deleted,
        )
    }

    fun recountGames(games: List<Game>, history: List<HistoryGame>, expansions: List<HistoryGameExpansion>): List<Game> {
        val sessionsByName = history.groupingBy { it.gameName }.eachCount()
        val expansionPlays = expansions.groupingBy { it.expansionId }.eachCount()
        return games.map { it.copy(games = (sessionsByName[it.name] ?: 0) + (expansionPlays[it.id] ?: 0)) }
    }

    // Player.winRatio holds the win count, not a ratio.
    fun recountPlayers(players: List<Player>, history: List<HistoryGame>): List<Player> =
        players.map { player ->
            val played = history.filter { player.name in it.listOfPlayer }
            player.copy(games = played.size, winRatio = played.count { it.isWinner(player.name) })
        }

    private class Picked<T>(val value: T, val fromLocal: Boolean)

    private fun <T : Any> pickNewer(
        local: List<T>,
        remote: List<T>,
        deletedAt: Map<String, Long>,
        id: (T) -> String,
        updatedAt: (T) -> Long,
    ): List<Picked<T>> {
        val localById = local.associateBy(id)
        val remoteById = remote.associateBy(id)
        return (localById.keys + remoteById.keys).mapNotNull { key ->
            val localValue = localById[key]
            val remoteValue = remoteById[key]
            val picked = when {
                remoteValue == null -> Picked(localValue!!, fromLocal = true)
                localValue == null || updatedAt(remoteValue) > updatedAt(localValue) -> Picked(remoteValue, fromLocal = false)
                else -> Picked(localValue, fromLocal = true)
            }
            picked.takeIf { deletedAt[key]?.let { deleted -> updatedAt(it.value) > deleted } ?: true }
        }
    }
}
