// Tier 3 — Room DAO integration tests (runs on device/emulator, NOT JVM)
//
// Required deps to add before implementing:
//   androidTestImplementation("androidx.room:room-testing:2.7.1")
//     (match your room version from libs.versions.toml)
//
// Setup pattern:
//   @Before: val context = ApplicationProvider.getApplicationContext<Context>()
//            db = Room.inMemoryDatabaseBuilder(context, Db::class.java)
//                .allowMainThreadQueries()   // required so suspend fns run on test thread
//                .build()
//            dao = db.gameDao()              // verify the method name in Db.kt
//   @After:  db.close()
//
// All DAO functions are suspend — wrap each call in runBlocking { } or runTest { }.
// runBlocking is available from kotlinx-coroutines-core (already a transitive dep).
// runTest is preferred if you add kotlinx-coroutines-test.
//
// Import: androidx.test.ext.junit.runners.AndroidJUnit4    (already a dep)
//         androidx.test.runner.AndroidJUnit4               (same, alternate import path)
//         org.junit.runner.RunWith
//         androidx.room.Room
//         com.goskar.boardgame.data.db.Db                  (your Room database class)
//         com.goskar.boardgame.data.db.GameDao
//         com.goskar.boardgame.data.models.Game

package com.goskar.boardgame.data.db

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// @RunWith(AndroidJUnit4::class)
class GameDaoTest {

    // private lateinit var db: Db
    // private lateinit var dao: GameDao

    @Before
    fun setUp() {
        // val context = ApplicationProvider.getApplicationContext<Context>()
        // db = Room.inMemoryDatabaseBuilder(context, Db::class.java)
        //     .allowMainThreadQueries()
        //     .build()
        // dao = db.gameDao()
    }

    @After
    fun tearDown() {
        // db.close()
    }

    // -------------------------------------------------------------------------
    // Helper: factory for a minimal Game entity
    //   fun game(id: String = UUID.randomUUID().toString(), name: String = "Test",
    //            expansion: Boolean = false, cooperate: Boolean = false,
    //            baseGame: String = "", minPlayer: String = "2",
    //            maxPlayer: String = "4", games: Int = 0) = Game(...)
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // insert() + getAll()
    // -------------------------------------------------------------------------

    @Test
    fun insert_oneGame_getAllReturnsThatGame() {
        // Given: a single Game entity
        // When:  dao.insert(game) is called
        // Then:  dao.getAll() returns a list of size 1
        //        the returned game matches the inserted game (check id, name, etc.)
    }

    @Test
    fun insert_onConflictReplace_duplicateIdOverwritesPreviousEntry() {
        // Given: two Game entities with the SAME id but different names
        // When:  dao.insert(game1) then dao.insert(game2) are called (same id)
        // Then:  dao.getAll() returns a list of size 1
        //        the game in the list has game2's name (the second insert replaced the first)
        // Note:  onConflict = OnConflictStrategy.REPLACE is set on this DAO
    }

    @Test
    fun getAll_emptyDatabase_returnsEmptyList() {
        // Given: no games have been inserted
        // When:  dao.getAll() is called
        // Then:  result is an empty list (not null)
    }

    // -------------------------------------------------------------------------
    // insertAll()
    // -------------------------------------------------------------------------

    @Test
    fun insertAll_multipleGames_getAllReturnsAll() {
        // Given: a list of 3 Game entities
        // When:  dao.insertAll(list) is called
        // Then:  dao.getAll() returns all 3 games
    }

    @Test
    fun insertAll_emptyList_databaseRemainsEmpty() {
        // Given: emptyList<Game>()
        // When:  dao.insertAll(emptyList()) is called
        // Then:  dao.getAll() is still empty (no crash)
    }

    // -------------------------------------------------------------------------
    // delete()
    // -------------------------------------------------------------------------

    @Test
    fun delete_existingGame_removesItFromDatabase() {
        // Given: a game is inserted
        // When:  dao.delete(game) is called
        // Then:  dao.getAll() is empty
    }

    @Test
    fun delete_oneOfMultipleGames_onlyThatGameIsRemoved() {
        // Given: 3 games are inserted
        // When:  dao.delete(game[1]) is called
        // Then:  dao.getAll() contains game[0] and game[2] (size == 2)
    }

    // -------------------------------------------------------------------------
    // edit()
    // -------------------------------------------------------------------------

    @Test
    fun edit_existingGame_updatesAllChangedFields() {
        // Given: a game is inserted with name="Chess", games=0
        // When:  dao.edit(game.copy(name="Chess 2", games=5)) is called
        // Then:  dao.getAll()[0].name  == "Chess 2"
        //        dao.getAll()[0].games == 5
    }

    @Test
    fun edit_onlyTargetGameIsUpdated() {
        // Given: 2 games inserted (game1, game2)
        // When:  dao.edit(game1.copy(name="Updated")) is called
        // Then:  dao.getAll() contains a game with name "Updated" (game1)
        //        and a game with game2's original name (game2 unchanged)
    }

    // -------------------------------------------------------------------------
    // deleteAll()
    // -------------------------------------------------------------------------

    @Test
    fun deleteAll_multipleGames_databaseIsEmpty() {
        // Given: 3 games are inserted
        // When:  dao.deleteAll() is called
        // Then:  dao.getAll() is empty
    }

    @Test
    fun deleteAll_emptyDatabase_doesNotCrash() {
        // Given: no games have been inserted
        // When:  dao.deleteAll() is called
        // Then:  no exception is thrown, dao.getAll() is still empty
    }
}
