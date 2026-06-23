// Tier 3 — Room DAO integration tests for Player entity (runs on device/emulator)
//
// Required deps to add before implementing:
//   androidTestImplementation("androidx.room:room-testing:2.7.1")
//     (match your room version from libs.versions.toml)
//
// Setup pattern: identical to GameDaoTest
//   @Before: db  = Room.inMemoryDatabaseBuilder(context, Db::class.java)
//                       .allowMainThreadQueries().build()
//            dao = db.playerDao()   // verify the method name in Db.kt
//   @After:  db.close()
//
// All DAO functions are suspend — wrap in runBlocking { } or runTest { }.
//
// Player entity fields:
//   name: String, games: Int, winRatio: Int, description: String,
//   selected: Boolean, id: String (PrimaryKey, default = UUID)

package com.goskar.boardgame.data.db

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// @RunWith(AndroidJUnit4::class)
class PlayerDaoTest {

    // private lateinit var db: Db
    // private lateinit var dao: PlayerDao

    @Before
    fun setUp() {
        // val context = ApplicationProvider.getApplicationContext<Context>()
        // db = Room.inMemoryDatabaseBuilder(context, Db::class.java)
        //     .allowMainThreadQueries()
        //     .build()
        // dao = db.playerDao()
    }

    @After
    fun tearDown() {
        // db.close()
    }

    // -------------------------------------------------------------------------
    // Helper: factory for a minimal Player entity
    //   fun player(id: String = UUID.randomUUID().toString(), name: String = "Alice",
    //              games: Int = 0, winRatio: Int = 0, description: String = "",
    //              selected: Boolean = false) = Player(...)
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // insert() + getAll()
    // -------------------------------------------------------------------------

    @Test
    fun insert_onePlayer_getAllReturnsThatPlayer() {
        // Given: a single Player entity
        // When:  dao.insert(player) is called
        // Then:  dao.getAll() returns a list of size 1
        //        the returned player matches on all fields (name, games, winRatio, etc.)
    }

    @Test
    fun insert_onConflictReplace_duplicateIdOverwritesPreviousEntry() {
        // Given: two Players with the SAME id but different names ("Alice" vs "Bob")
        // When:  dao.insert(player1) then dao.insert(player2) are called (same id)
        // Then:  dao.getAll() has size 1 with name "Bob" (second replaces first)
    }

    @Test
    fun getAll_emptyDatabase_returnsEmptyList() {
        // Given: no players inserted
        // When:  dao.getAll() is called
        // Then:  result is an empty list (not null)
    }

    // -------------------------------------------------------------------------
    // insertAll()
    // -------------------------------------------------------------------------

    @Test
    fun insertAll_multiplePlayers_getAllReturnsAll() {
        // Given: a list of 3 Player entities
        // When:  dao.insertAll(list) is called
        // Then:  dao.getAll() returns all 3 players
    }

    @Test
    fun insertAll_emptyList_databaseRemainsEmpty() {
        // Given: emptyList<Player>()
        // When:  dao.insertAll(emptyList()) is called
        // Then:  dao.getAll() is still empty (no crash)
    }

    // -------------------------------------------------------------------------
    // delete()
    // -------------------------------------------------------------------------

    @Test
    fun delete_existingPlayer_removesItFromDatabase() {
        // Given: a player is inserted
        // When:  dao.delete(player) is called
        // Then:  dao.getAll() is empty
    }

    @Test
    fun delete_oneOfMultiplePlayers_onlyThatPlayerIsRemoved() {
        // Given: 3 players inserted (alice, bob, carol)
        // When:  dao.delete(bob) is called
        // Then:  dao.getAll() contains alice and carol (size == 2)
    }

    // -------------------------------------------------------------------------
    // edit()
    // -------------------------------------------------------------------------

    @Test
    fun edit_updatesName() {
        // Given: player inserted with name="Alice"
        // When:  dao.edit(player.copy(name="Alicia")) is called
        // Then:  dao.getAll()[0].name == "Alicia"
    }

    @Test
    fun edit_updatesGamesAndWinRatio() {
        // Given: player inserted with games=0, winRatio=0
        // When:  dao.edit(player.copy(games=10, winRatio=3)) is called
        // Then:  dao.getAll()[0].games    == 10
        //        dao.getAll()[0].winRatio == 3
    }

    @Test
    fun edit_updatesSelectedFlag() {
        // Given: player inserted with selected=false
        // When:  dao.edit(player.copy(selected=true)) is called
        // Then:  dao.getAll()[0].selected == true
        // Note:  this is a mutable var field — verify Room persists it correctly
    }

    @Test
    fun edit_onlyTargetPlayerIsUpdated() {
        // Given: 2 players inserted (alice, bob)
        // When:  dao.edit(alice.copy(name="Alicia")) is called
        // Then:  one player has name "Alicia", the other still has bob's original name
    }

    // -------------------------------------------------------------------------
    // deleteAll()
    // -------------------------------------------------------------------------

    @Test
    fun deleteAll_multiplePlayers_databaseIsEmpty() {
        // Given: 3 players inserted
        // When:  dao.deleteAll() is called
        // Then:  dao.getAll() is empty
    }

    @Test
    fun deleteAll_emptyDatabase_doesNotCrash() {
        // Given: no players inserted
        // When:  dao.deleteAll() is called
        // Then:  no exception is thrown, dao.getAll() is still empty
    }
}
