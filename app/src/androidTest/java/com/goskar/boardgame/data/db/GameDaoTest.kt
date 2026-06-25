package com.goskar.boardgame.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goskar.boardgame.data.models.Game
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class GameDaoTest {

    private lateinit var db: Db
    private lateinit var dao: GameDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, Db::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.gameDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // -------------------------------------------------------------------------
    // Helper: factory for a minimal Game entity
    // -------------------------------------------------------------------------
    private fun game(
        id: String = UUID.randomUUID().toString(),
        name: String = "Test Game",
        expansion: Boolean = false,
        games: Int = 0
    ) = Game(
        id = id,
        name = name,
        expansion = expansion,
        cooperate = false,
        baseGame = "",
        minPlayer = "2",
        maxPlayer = "4",
        games = games
    )

    // -------------------------------------------------------------------------
    // insert() + getAll()
    // -------------------------------------------------------------------------

    @Test
    fun insert_oneGame_getAllReturnsThatGame() = runTest {
        val chess = game(name = "Chess", games = 10)

        dao.insert(chess)

        val allGames = dao.getAll()
        assertEquals(1, allGames.size)
        val result = allGames[0]
        assertEquals(chess.id, result.id)
        assertEquals("Chess", result.name)
        assertEquals(10, result.games)
    }

    @Test
    fun insert_onConflictReplace_duplicateIdOverwritesPreviousEntry() = runTest {
        val chess = game(id = "id-01", name = "Chess", games = 10)
        dao.insert(chess)

        val avel = game(id = "id-01", name = "Avel", games = 5)
        dao.insert(avel)

        val allGames = dao.getAll()
        assertEquals(1, allGames.size)
        assertEquals("Avel", allGames[0].name)
        assertEquals(5, allGames[0].games)
    }

    @Test
    fun getAll_emptyDatabase_returnsEmptyList() = runTest {
        val allGames = dao.getAll()
        assertEquals(0, allGames.size)
    }

    // -------------------------------------------------------------------------
    // insertAll()
    // -------------------------------------------------------------------------

    @Test
    fun insertAll_multipleGames_getAllReturnsAll() = runTest {
        val games = listOf(game(name = "G1"), game(name = "G2"), game(name = "G3"))

        dao.insertAll(games)

        val allGames = dao.getAll()
        assertEquals(3, allGames.size)
    }

    @Test
    fun insertAll_emptyList_databaseRemainsEmpty() = runTest {
        dao.insertAll(emptyList())

        val allGames = dao.getAll()
        assertEquals(0, allGames.size)
    }

    // -------------------------------------------------------------------------
    // delete()
    // -------------------------------------------------------------------------

    @Test
    fun delete_existingGame_removesItFromDatabase() = runTest {
        val chess = game(name = "Chess")
        dao.insert(chess)

        dao.delete(chess)

        val allGames = dao.getAll()
        assertEquals(0, allGames.size)
    }

    @Test
    fun delete_oneOfMultipleGames_onlyThatGameIsRemoved() = runTest {
        val chess = game(name = "Chess")
        val avel = game(name = "Avel")
        dao.insertAll(listOf(chess, avel))

        dao.delete(chess)

        val allGames = dao.getAll()
        assertEquals(1, allGames.size)
        assertEquals("Avel", allGames[0].name)
    }

    // -------------------------------------------------------------------------
    // edit()
    // -------------------------------------------------------------------------

    @Test
    fun edit_existingGame_updatesAllChangedFields() = runTest {
        val chess = game(name = "Chess", games = 10)
        dao.insert(chess)

        dao.edit(chess.copy(name = "Chess 2", games = 7))

        val allGames = dao.getAll()
        assertEquals("Chess 2", allGames[0].name)
        assertEquals(7, allGames[0].games)
    }

    @Test
    fun edit_onlyTargetGameIsUpdated() = runTest {
        val chess = game(name = "Chess")
        val avel = game(name = "Avel")
        dao.insertAll(listOf(chess, avel))

        dao.edit(chess.copy(name = "Updated"))

        val allGames = dao.getAll()
        assertEquals(true, allGames.any { it.name == "Updated" })
        assertEquals(true, allGames.any { it.name == "Avel" })
    }

    // -------------------------------------------------------------------------
    // deleteAll()
    // -------------------------------------------------------------------------

    @Test
    fun deleteAll_multipleGames_databaseIsEmpty() = runTest {
        dao.insertAll(listOf(game(), game()))

        dao.deleteAll()

        assertEquals(0, dao.getAll().size)
    }

    @Test
    fun deleteAll_emptyDatabase_doesNotCrash() = runTest {
        dao.deleteAll()
        assertEquals(0, dao.getAll().size)
    }
}
