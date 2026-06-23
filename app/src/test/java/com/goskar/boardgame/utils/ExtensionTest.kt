package com.goskar.boardgame.utils

import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.models.HistoryGameFirebase
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ExtensionTest {

    // -------------------------------------------------------------------------
    // convertHistoryGameListToDto  (List<HistoryGameFirebase> → List<HistoryGame>)
    // -------------------------------------------------------------------------

    @Test
    fun convertHistoryGameListToDto_singleItem_parsesDateCorrectly() {
        val input = listOf(
            HistoryGameFirebase(
                gameName = "Chess",
                winner = "Alice",
                gameData = "2024-03-15",
                listOfPlayer = listOf("Alice", "Bob"),
                description = "",
                id = "id-1"
            )
        )

        val result = convertHistoryGameListToDto(input)

        assertEquals(LocalDate.of(2024, 3, 15), result[0].gameData)
    }

    @Test
    fun convertHistoryGameListToDto_singleItem_preservesAllOtherFields() {
        val input = listOf(
            HistoryGameFirebase(
                gameName = "Chess",
                winner = "Alice",
                gameData = "2024-01-01",
                listOfPlayer = listOf("Alice", "Bob"),
                description = "Great game",
                id = "fixed-id"
            )
        )

        val result = convertHistoryGameListToDto(input)

        assertEquals("Chess",                result[0].gameName)
        assertEquals("Alice",                result[0].winner)
        assertEquals(listOf("Alice", "Bob"), result[0].listOfPlayer)
        assertEquals("Great game",           result[0].description)
        assertEquals("fixed-id",             result[0].id)
    }

    @Test
    fun convertHistoryGameListToDto_multipleItems_mapsEachItemIndependently() {
        val input = listOf(
            HistoryGameFirebase(gameName = "Chess",    winner = "Alice", gameData = "2023-01-01", listOfPlayer = emptyList(), description = "", id = "id-1"),
            HistoryGameFirebase(gameName = "Catan",    winner = "Bob",   gameData = "2023-06-15", listOfPlayer = emptyList(), description = "", id = "id-2"),
            HistoryGameFirebase(gameName = "Wingspan", winner = "Carol", gameData = "2024-12-31", listOfPlayer = emptyList(), description = "", id = "id-3"),
        )

        val result = convertHistoryGameListToDto(input)

        assertEquals(3, result.size)
        assertEquals(LocalDate.of(2023,  1,  1), result[0].gameData)
        assertEquals(LocalDate.of(2023,  6, 15), result[1].gameData)
        assertEquals(LocalDate.of(2024, 12, 31), result[2].gameData)
        assertEquals("id-1", result[0].id)
        assertEquals("id-2", result[1].id)
        assertEquals("id-3", result[2].id)
    }

    // -------------------------------------------------------------------------
    // convertHistoryGameListToFirebase  (List<HistoryGame> → List<HistoryGameFirebase>)
    // -------------------------------------------------------------------------

    @Test
    fun convertHistoryGameListToFirebase_singleItem_formatsDateAsIsoString() {
        val input = listOf(
            HistoryGame(
                gameName = "Chess",
                winner = "Alice",
                gameData = LocalDate.of(2024, 3, 15),
                listOfPlayer = listOf("Alice"),
                description = "",
                id = "id-1"
            )
        )

        val result = convertHistoryGameListToFirebase(input)

        assertEquals("2024-03-15", result[0].gameData)
    }

    @Test
    fun convertHistoryGameListToFirebase_singleItem_preservesAllOtherFields() {
        val input = listOf(
            HistoryGame(
                gameName = "Chess",
                winner = "Alice",
                gameData = LocalDate.of(2024, 1, 1),
                listOfPlayer = listOf("Alice", "Bob"),
                description = "Great game",
                id = "fixed-id"
            )
        )

        val result = convertHistoryGameListToFirebase(input)

        assertEquals("Chess",                result[0].gameName)
        assertEquals("Alice",                result[0].winner)
        assertEquals(listOf("Alice", "Bob"), result[0].listOfPlayer)
        assertEquals("Great game",           result[0].description)
        assertEquals("fixed-id",             result[0].id)
    }

    // -------------------------------------------------------------------------
    // Round-trip
    // -------------------------------------------------------------------------

    @Test
    fun roundTrip_dtoToFirebaseAndBack_preservesAllData() {
        val original = listOf(
            HistoryGame(
                gameName = "Chess",
                winner = "Alice",
                gameData = LocalDate.of(2024, 6, 15),
                listOfPlayer = listOf("Alice", "Bob"),
                description = "Round-trip test",
                id = "rt-id-1"
            )
        )

        val result = convertHistoryGameListToDto(convertHistoryGameListToFirebase(original))

        // HistoryGame is a data class — assertEquals compares all fields including gameData (LocalDate)
        assertEquals(original, result)
    }
}
