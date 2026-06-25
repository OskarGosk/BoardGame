package com.goskar.boardgame.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goskar.boardgame.data.models.Player
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PlayerDaoTest {

    private lateinit var db: Db
    private lateinit var dao: PlayerDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, Db::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.playerDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // -------------------------------------------------------------------------
    // Helper: factory for a minimal Player entity
    // -------------------------------------------------------------------------
    private fun player(
        id: String = UUID.randomUUID().toString(),
        name: String = "Alice",
        games: Int = 10,
        winRatio: Int = 8,
        description: String = "",
        selected: Boolean = false
    ) = Player(
        id = id,
        name = name,
        games = games,
        winRatio = winRatio,
        description = description,
        selected = selected
    )

    // -------------------------------------------------------------------------
    // insert() + getAll()
    // -------------------------------------------------------------------------

    @Test
    fun insert_onePlayer_getAllReturnsThatPlayer() = runTest {
        val oskar = player(name = "Oskar", games = 10, winRatio = 5)

        dao.insert(oskar)

        val allPlayers = dao.getAll()
        assertEquals(1, allPlayers.size)
        val result = allPlayers[0]
        assertEquals("Oskar", result.name)
        assertEquals(10, result.games)
        assertEquals(5, result.winRatio)
    }

    @Test
    fun insert_onConflictReplace_duplicateIdOverwritesPreviousEntry() = runTest {
        val id = "player-01"
        val player1 = player(id = id, name = "Oskar")
        dao.insert(player1)

        val player2 = player(id = id, name = "Alice")
        dao.insert(player2)

        val allPlayers = dao.getAll()
        assertEquals(1, allPlayers.size)
        assertEquals("Alice", allPlayers[0].name)
    }

    @Test
    fun getAll_emptyDatabase_returnsEmptyList() = runTest {
        val allPlayers = dao.getAll()
        assertEquals(0, allPlayers.size)
    }

    // -------------------------------------------------------------------------
    // insertAll()
    // -------------------------------------------------------------------------

    @Test
    fun insertAll_multiplePlayers_getAllReturnsAll() = runTest {
        val players = listOf(player(name = "Oskar"), player(name = "Alice"), player(name = "Nicole"))

        dao.insertAll(players)

        val allPlayers = dao.getAll()
        assertEquals(3, allPlayers.size)
    }

    @Test
    fun insertAll_emptyList_databaseRemainsEmpty() = runTest {
        dao.insertAll(emptyList())

        val allPlayers = dao.getAll()
        assertEquals(0, allPlayers.size)
    }

    // -------------------------------------------------------------------------
    // delete()
    // -------------------------------------------------------------------------

    @Test
    fun delete_existingPlayer_removesItFromDatabase() = runTest {
        val p1 = player(name = "Oskar")
        dao.insert(p1)

        dao.delete(p1)

        val allPlayers = dao.getAll()
        assertEquals(0, allPlayers.size)
    }

    @Test
    fun delete_oneOfMultiplePlayers_onlyThatPlayerIsRemoved() = runTest {
        val p1 = player(name = "Oskar")
        val p2 = player(name = "Alice")
        dao.insertAll(listOf(p1, p2))

        dao.delete(p1)

        val allPlayers = dao.getAll()
        assertEquals(1, allPlayers.size)
        assertEquals("Alice", allPlayers[0].name)
    }

    // -------------------------------------------------------------------------
    // edit()
    // -------------------------------------------------------------------------

    @Test
    fun edit_updatesAllFields() = runTest {
        val p1 = player(name = "Oskar", games = 0, winRatio = 0)
        dao.insert(p1)

        dao.edit(p1.copy(name = "Alice", games = 10, winRatio = 5))

        val result = dao.getAll()[0]
        assertEquals("Alice", result.name)
        assertEquals(10, result.games)
        assertEquals(5, result.winRatio)
    }

    @Test
    fun edit_updatesSelectedFlag() = runTest {
        val p1 = player(selected = false)
        dao.insert(p1)

        dao.edit(p1.copy(selected = true))

        val result = dao.getAll()[0]
        assertEquals(true, result.selected)
    }

    @Test
    fun edit_onlyTargetPlayerIsUpdated() = runTest {
        val p1 = player(name = "Oskar")
        val p2 = player(name = "Alice")
        dao.insertAll(listOf(p1, p2))

        dao.edit(p1.copy(name = "Updated"))

        val allPlayers = dao.getAll()
        assertTrue(allPlayers.any { it.name == "Updated" })
        assertTrue(allPlayers.any { it.name == "Alice" })
    }

    // -------------------------------------------------------------------------
    // deleteAll()
    // -------------------------------------------------------------------------

    @Test
    fun deleteAll_multiplePlayers_databaseIsEmpty() = runTest {
        dao.insertAll(listOf(player(), player()))

        dao.deleteAll()

        assertEquals(0, dao.getAll().size)
    }

    @Test
    fun deleteAll_emptyDatabase_doesNotCrash() = runTest {
        dao.deleteAll()
        assertEquals(0, dao.getAll().size)
    }
}
