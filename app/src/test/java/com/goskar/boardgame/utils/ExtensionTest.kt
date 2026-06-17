// Tier 1 — pure Kotlin, no mocks or coroutines needed
// Required deps: none beyond existing testImplementation(libs.junit)
//
// Note: checkAndRequestPermissionWithClick() and getCameraProvider() both require
// Android Context / CameraX and cannot be tested as JVM unit tests.
// Cover only the two pure conversion functions below.

package com.goskar.boardgame.utils

import org.junit.Test

class ExtensionTest {

    // -------------------------------------------------------------------------
    // convertHistoryGameListToDto
    // Converts List<HistoryGameFirebase> → List<HistoryGame>
    // Key transform: gameData String (ISO-8601) → LocalDate.parse(gameData)
    // -------------------------------------------------------------------------

    @Test
    fun convertHistoryGameListToDto_emptyList_returnsEmptyList() {
        // Given: an empty List<HistoryGameFirebase>
        // When:  convertHistoryGameListToDto() is called
        // Then:  result is an empty list
    }

    @Test
    fun convertHistoryGameListToDto_singleItem_parsesDateCorrectly() {
        // Given: one HistoryGameFirebase with gameData = "2024-03-15"
        // When:  convertHistoryGameListToDto() is called
        // Then:  result[0].gameData == LocalDate.of(2024, 3, 15)
    }

    @Test
    fun convertHistoryGameListToDto_singleItem_preservesAllOtherFields() {
        // Given: one HistoryGameFirebase with known gameName, winner,
        //        listOfPlayer, description, and id
        // When:  convertHistoryGameListToDto() is called
        // Then:  result[0].gameName, winner, listOfPlayer, description, id
        //        all equal the original values unchanged
    }

    @Test
    fun convertHistoryGameListToDto_multipleItems_mapsEachItemIndependently() {
        // Given: a list of 3 HistoryGameFirebase entries with different dates
        // When:  convertHistoryGameListToDto() is called
        // Then:  result has size 3
        //        each item's gameData is the correctly parsed LocalDate
        //        order is preserved
    }

    @Test
    fun convertHistoryGameListToDto_emptyListOfPlayer_preservesEmptyList() {
        // Given: HistoryGameFirebase with listOfPlayer = emptyList()
        // When:  convertHistoryGameListToDto() is called
        // Then:  result[0].listOfPlayer is empty (no crash, no null)
    }

    // -------------------------------------------------------------------------
    // convertHistoryGameListToFirebase
    // Converts List<HistoryGame> → List<HistoryGameFirebase>
    // Key transform: gameData LocalDate → gameData.toString() (ISO-8601 string)
    // -------------------------------------------------------------------------

    @Test
    fun convertHistoryGameListToFirebase_emptyList_returnsEmptyList() {
        // Given: an empty List<HistoryGame>
        // When:  convertHistoryGameListToFirebase() is called
        // Then:  result is an empty list
    }

    @Test
    fun convertHistoryGameListToFirebase_singleItem_formatsDateAsIsoString() {
        // Given: one HistoryGame with gameData = LocalDate.of(2024, 3, 15)
        // When:  convertHistoryGameListToFirebase() is called
        // Then:  result[0].gameData == "2024-03-15"
        //        (LocalDate.toString() produces ISO-8601: "YYYY-MM-DD")
    }

    @Test
    fun convertHistoryGameListToFirebase_singleItem_preservesAllOtherFields() {
        // Given: one HistoryGame with known gameName, winner,
        //        listOfPlayer, description, and id
        // When:  convertHistoryGameListToFirebase() is called
        // Then:  result[0].gameName, winner, listOfPlayer, description, id
        //        all equal the original values unchanged
    }

    @Test
    fun roundTrip_dtoToFirebaseAndBack_preservesAllData() {
        // Given: a list of HistoryGame entries
        // When:  convertHistoryGameListToFirebase() then convertHistoryGameListToDto()
        // Then:  the final list equals the original (date survives both conversions)
        // Note:  this catches any date format mismatch between the two functions
    }
}
