// Tier 2 — player selection, expansion toggling, game variant, setGameData filtering
//
// Required deps to add before implementing:
//   testImplementation("io.mockk:mockk:1.13.14")
//   testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
//
// Setup pattern:
//   @Before: Dispatchers.setMain(UnconfinedTestDispatcher())
//            playerDbRepository      = mockk<PlayerDbRepository>()
//            gameDbRepository        = mockk<GameDbRepository>()
//            gamesHistoryDbRepository= mockk<GamesHistoryDbRepository>()
//            getAllGameUseCase        = mockk<GetAllGameUseCase>()
//            viewModel = GamePlayViewModel(playerDbRepository, gameDbRepository,
//                                         gamesHistoryDbRepository, getAllGameUseCase)
//            Note: no init block — construction is safe without pre-stubbing.
//   @After:  Dispatchers.resetMain()
//
// Private functions (setGameData, validateAddHistoryGameData, validateEditAllPlayer, etc.)
// are tested indirectly via the public functions that call them:
//   setGameData()               → tested via getAllGame()
//   validateAddHistoryGameData  → tested via validateAllData() — but this needs Context;
//                                  consider Robolectric for validateAllData() tests
//
// Focus here is on the pure-state functions that don't need Context.

package com.goskar.boardgame.ui.gamesList.play

import org.junit.After
import org.junit.Before
import org.junit.Test

class GamePlayViewModelTest {

    @Before
    fun setUp() {
        // Dispatchers.setMain(UnconfinedTestDispatcher())
        // playerDbRepository       = mockk<PlayerDbRepository>()
        // gameDbRepository         = mockk<GameDbRepository>()
        // gamesHistoryDbRepository = mockk<GamesHistoryDbRepository>()
        // getAllGameUseCase         = mockk<GetAllGameUseCase>()
        // viewModel = GamePlayViewModel(playerDbRepository, gameDbRepository,
        //                               gamesHistoryDbRepository, getAllGameUseCase)
    }

    @After
    fun tearDown() {
        // Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // selectedPlayer(player)
    // Toggles player.selected by id, updates countSelectedPlayer
    // This was the bug fixed in task #8 — tests act as a regression guard.
    // -------------------------------------------------------------------------

    @Test
    fun selectedPlayer_unselectedPlayer_becomesSelected() {
        // Given: state.playerList = [player(id="1", selected=false), player(id="2", selected=false)]
        //        set via viewModel.update(state.copy(playerList = ...))
        // When:  selectedPlayer(player(id="1")) is called
        // Then:  state.playerList[0].selected == true   (player "1" toggled)
        //        state.playerList[1].selected == false  (player "2" unchanged)
        //        state.countSelectedPlayer    == 1
    }

    @Test
    fun selectedPlayer_selectedPlayer_becomesDeselected() {
        // Given: state.playerList = [player(id="1", selected=true)]
        // When:  selectedPlayer(player(id="1")) is called
        // Then:  state.playerList[0].selected  == false
        //        state.countSelectedPlayer      == 0
    }

    @Test
    fun selectedPlayer_onlyTargetPlayerChanges() {
        // Given: 3 players, all selected=false
        // When:  selectedPlayer(player[1]) is called
        // Then:  player[0].selected == false
        //        player[1].selected == true
        //        player[2].selected == false
    }

    @Test
    fun selectedPlayer_calledTwiceOnSamePlayer_returnsToOriginalState() {
        // Given: player(id="1", selected=false)
        // When:  selectedPlayer(player) called twice
        // Then:  player.selected == false (double-toggle round-trips)
        //        countSelectedPlayer == 0
    }

    @Test
    fun selectedPlayer_countReflectsTotalSelectedAcrossAllPlayers() {
        // Given: 4 players, all selected=false
        // When:  selectedPlayer(player[0]), selectedPlayer(player[2]) called sequentially
        // Then:  countSelectedPlayer == 2
    }

    // -------------------------------------------------------------------------
    // selectExpansion(expansionId)
    // Toggles isSelected for the matching ExpansionGameUiState by game.id
    // -------------------------------------------------------------------------

    @Test
    fun selectExpansion_unselectedExpansion_becomesSelected() {
        // Given: state.gameList = [ExpansionGameUiState(game(id="exp-1"), isSelected=false)]
        // When:  selectExpansion("exp-1") is called
        // Then:  state.gameList[0].isSelected == true
    }

    @Test
    fun selectExpansion_selectedExpansion_becomesDeselected() {
        // Given: state.gameList = [ExpansionGameUiState(game(id="exp-1"), isSelected=true)]
        // When:  selectExpansion("exp-1") is called
        // Then:  state.gameList[0].isSelected == false
    }

    @Test
    fun selectExpansion_onlyTargetExpansionChanges() {
        // Given: 3 ExpansionGameUiState entries, all isSelected=false
        // When:  selectExpansion(gameList[1].game.id) is called
        // Then:  gameList[0].isSelected == false
        //        gameList[1].isSelected == true
        //        gameList[2].isSelected == false
    }

    // -------------------------------------------------------------------------
    // setGameVariant()
    // Sets state.gameVariant based on state.game.cooperate
    // -------------------------------------------------------------------------

    @Test
    fun setGameVariant_cooperateGame_setsCoopVariant() {
        // Given: state.game = game(cooperate=true)
        //        set via viewModel.update(state.copy(game = cooperateGame))
        // When:  setGameVariant() is called
        // Then:  state.gameVariant == R.string.history_coop
    }

    @Test
    fun setGameVariant_nonCooperateGame_setsNormalVariant() {
        // Given: state.game = game(cooperate=false)
        // When:  setGameVariant() is called
        // Then:  state.gameVariant == R.string.history_normal
    }

    @Test
    fun setGameVariant_nullGame_setsNormalVariant() {
        // Given: state.game == null (default)
        // When:  setGameVariant() is called
        // Then:  state.gameVariant == R.string.history_normal
        //        (when(null?.cooperate) → when(null) → else → R.string.history_normal)
    }

    // -------------------------------------------------------------------------
    // getAllGame() + setGameData() (private, tested indirectly)
    //
    // setGameData() behaviour:
    //   If state.game.expansion == false (base game):
    //     → gameList is filtered to expansions whose baseGameId == game.id
    //   If state.game.expansion == true (the user started from an expansion):
    //     → resolve state.game to its base (firstOrNull { it.game.id == game.baseGameId })
    //     → filter gameList to expansions of that same baseGameId
    //     → auto-select the original expansion (the one the user tapped)
    // -------------------------------------------------------------------------

    @Test
    fun getAllGame_baseGame_gameListFilteredToDirectExpansions() {
        // Given: getAllGameUseCase.invoke() returns a list containing:
        //          baseGame(id="base-1", expansion=false)
        //          expansion1(id="exp-A", expansion=true, baseGameId="base-1")
        //          expansion2(id="exp-B", expansion=true, baseGameId="base-1")
        //          unrelated(id="other", expansion=false)
        //        state.game = baseGame (set via update() BEFORE calling getAllGame())
        // When:  getAllGame() is called
        // Then:  state.gameList contains only exp-A and exp-B
        //        state.game is still the baseGame (unchanged for base-game path)
    }

    @Test
    fun getAllGame_expansionGame_resolvesToBaseAndAutoSelectsOriginalExpansion() {
        // Given: getAllGameUseCase.invoke() returns:
        //          baseGame(id="base-1", expansion=false)
        //          expansion1(id="exp-A", expansion=true, baseGameId="base-1")
        //          expansion2(id="exp-B", expansion=true, baseGameId="base-1")
        //        state.game = expansion1 (expansion=true, baseGameId="base-1")
        // When:  getAllGame() is called
        // Then:  state.game == baseGame     (resolved to the base)
        //        state.gameList contains exp-A and exp-B
        //        exp-A.isSelected == true   (auto-selected because it was the starting expansion)
        //        exp-B.isSelected == false
    }

    // -------------------------------------------------------------------------
    // getAllPlayer()
    // -------------------------------------------------------------------------

    @Test
    fun getAllPlayer_success_setsPlayerListAndClearsError() {
        // Given: playerDbRepository.getAllPlayer() returns Success([player1, player2])
        // When:  getAllPlayer() is called
        // Then:  state.playerList   == [player1, player2]
        //        state.errorVisible == false
    }

    @Test
    fun getAllPlayer_error_showsError() {
        // Given: playerDbRepository.getAllPlayer() returns RequestResult.Error(Throwable())
        // When:  getAllPlayer() is called
        // Then:  state.errorVisible == true
    }
}
