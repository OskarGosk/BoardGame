// Tier 2 — use-case logic: branching on two RequestResult values and joining data
//
// Required deps to add before implementing:
//   testImplementation("io.mockk:mockk:1.13.14")
//   testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
//
// Setup pattern:
//   @Before: repository = mockk<GamesHistoryDbRepository>()
//            useCase    = GetHistoryWithExpansionUseCase(repository)
//
// All tests call: runTest { val result = useCase() }
// The function calls TWO repository methods sequentially:
//   1. repository.getAllHistoryGame()    → RequestResult<List<HistoryGame>>
//   2. repository.getAllHistoryGameExpansion() → RequestResult<List<HistoryGameExpansion>>
//
// Logic summary (from the when-expression in invoke()):
//   history=Error                         → return that Error  (expansion NOT checked)
//   history=Success, expansion=Error      → return expansion's Error
//   history=Success, expansion=Success    → join: each HistoryGame gets the expansions
//                                           whose historyGameId == game.id
//   else (both not Success/Error)         → Error("Coś poszło nie tak...")

package com.goskar.boardgame.data.useCase

import org.junit.Before
import org.junit.Test

class GetHistoryWithExpansionUseCaseTest {

    @Before
    fun setUp() {
        // repository = mockk<GamesHistoryDbRepository>()
        // useCase    = GetHistoryWithExpansionUseCase(repository)
    }

    // -------------------------------------------------------------------------
    // Success path — both repos return data
    // -------------------------------------------------------------------------

    @Test
    fun invoke_bothRepositoriesSucceed_returnsSuccessWithJoinedData() {
        // Given: repository.getAllHistoryGame() returns Success([game1, game2])
        //        game1.id = "id-1", game2.id = "id-2"
        //        repository.getAllHistoryGameExpansion() returns
        //          Success([expansion(historyGameId="id-1"), expansion(historyGameId="id-1"),
        //                   expansion(historyGameId="id-2")])
        // When:  useCase() is called
        // Then:  result is RequestResult.Success
        //        result.data has size 2
        //        result.data[0].history == game1
        //        result.data[0].expansion has size 2   (both belong to "id-1")
        //        result.data[1].history == game2
        //        result.data[1].expansion has size 1   (only the "id-2" expansion)
    }

    @Test
    fun invoke_gameHasNoMatchingExpansions_expansionListIsEmpty() {
        // Given: repository.getAllHistoryGame() returns Success([game1])
        //        repository.getAllHistoryGameExpansion() returns Success([]) (no expansions)
        // When:  useCase() is called
        // Then:  result.data[0].expansion is empty (not null, just empty)
    }

    @Test
    fun invoke_emptyHistoryList_returnsSuccessWithEmptyData() {
        // Given: repository.getAllHistoryGame() returns Success([])
        //        repository.getAllHistoryGameExpansion() returns Success([])
        // When:  useCase() is called
        // Then:  result is RequestResult.Success
        //        result.data is empty
    }

    // -------------------------------------------------------------------------
    // Error paths
    // -------------------------------------------------------------------------

    @Test
    fun invoke_historyRepositoryFails_returnsHistoryError() {
        // Given: repository.getAllHistoryGame() returns RequestResult.Error(Throwable("db error"))
        //        (getAllHistoryGameExpansion does not need to be stubbed — it won't be called)
        // When:  useCase() is called
        // Then:  result is RequestResult.Error
        //        the error is the same Throwable from the history repo
        //        repository.getAllHistoryGameExpansion() was NOT called
        //        (verify with mockk: verify(exactly = 0) { repository.getAllHistoryGameExpansion() })
    }

    @Test
    fun invoke_expansionRepositoryFails_returnsExpansionError() {
        // Given: repository.getAllHistoryGame() returns Success([game1])
        //        repository.getAllHistoryGameExpansion() returns RequestResult.Error(Throwable("exp error"))
        // When:  useCase() is called
        // Then:  result is RequestResult.Error
        //        the error is the same Throwable from the expansion repo
    }

    // -------------------------------------------------------------------------
    // Data isolation — expansions are correctly partitioned per game
    // -------------------------------------------------------------------------

    @Test
    fun invoke_expansionsForDifferentGames_noCrossContamination() {
        // Given: 3 history games (id-A, id-B, id-C)
        //        expansions: two belong to id-A, one to id-C, none to id-B
        // When:  useCase() is called
        // Then:  result.data[0] (id-A) has 2 expansions
        //        result.data[1] (id-B) has 0 expansions
        //        result.data[2] (id-C) has 1 expansion
        //        no expansion appears in more than one game's list
    }
}
