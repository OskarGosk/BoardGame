package com.goskar.boardgame.data.repository.dbRepository

import com.goskar.boardgame.data.db.GameDao
import com.goskar.boardgame.data.db.HistoryGameDao
import com.goskar.boardgame.data.db.PlayerDao
import com.goskar.boardgame.data.db.SyncDao
import com.goskar.boardgame.data.models.DeletedRecord
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.Player
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate

class DbRepositorySyncMetadataTest {

    private val now = 1_000L
    private val syncDao: SyncDao = mockk(relaxed = true)

    private val game = Game(
        name = "Root", expansion = false, cooperate = false, baseGame = "",
        minPlayer = "1", maxPlayer = "4", games = 0, id = "g1",
    )
    private val player = Player(name = "Ala", games = 0, winRatio = 0, description = "", selected = false, id = "p1")
    private val session = HistoryGame(
        gameName = "Root", winner = "Ala", gameData = LocalDate.of(2026, 1, 1),
        listOfPlayer = listOf("Ala"), description = "", id = "s1",
    )

    @Test
    fun gameWrites_stampUpdatedAt_andDeletesLeaveARecord() = runTest {
        val dao: GameDao = mockk(relaxed = true)
        val repository = GameDbRepositoryImpl(dao, syncDao, StandardTestDispatcher(testScheduler)) { now }

        repository.insertGame(game)
        repository.editGame(game)
        repository.insertAllGame(listOf(game))
        repository.deleteGame(game)

        coVerify(exactly = 1) { dao.insert(game.copy(updatedAt = now)) }
        coVerify(exactly = 1) { dao.edit(game.copy(updatedAt = now)) }
        coVerify(exactly = 1) { dao.insertAll(listOf(game)) }
        coVerify(exactly = 1) { syncDao.insertDeleted(DeletedRecord("g1", DeletedRecord.GAME, now)) }
    }

    @Test
    fun playerWrites_stampUpdatedAt_andDeletesLeaveARecord() = runTest {
        val dao: PlayerDao = mockk(relaxed = true)
        val repository = PlayerDbRepositoryImpl(dao, syncDao, StandardTestDispatcher(testScheduler)) { now }

        repository.insertPlayer(player)
        repository.editPlayer(player)
        repository.deletePlayer(player)

        coVerify(exactly = 1) { dao.insert(player.copy(updatedAt = now)) }
        coVerify(exactly = 1) { dao.edit(player.copy(updatedAt = now)) }
        coVerify(exactly = 1) { syncDao.insertDeleted(DeletedRecord("p1", DeletedRecord.PLAYER, now)) }
    }

    @Test
    fun sessionWrites_stampUpdatedAt_andDeletesLeaveARecord() = runTest {
        val dao: HistoryGameDao = mockk(relaxed = true)
        val repository = GamesHistoryDbRepositoryImpl(dao, mockk(relaxed = true), syncDao, StandardTestDispatcher(testScheduler)) { now }

        repository.insertHistoryGame(session)
        repository.editHistoryGame(session)
        repository.deleteHistoryGame(session)

        coVerify(exactly = 1) { dao.insert(session.copy(updatedAt = now)) }
        coVerify(exactly = 1) { dao.edit(session.copy(updatedAt = now)) }
        coVerify(exactly = 1) { syncDao.insertDeleted(DeletedRecord("s1", DeletedRecord.HISTORY_GAME, now)) }
    }
}
