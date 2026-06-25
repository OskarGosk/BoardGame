package com.goskar.boardgame.data.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    // -------------------------------------------------------------------------
    // List<String> <-> String  (Gson)
    // -------------------------------------------------------------------------

    @Test
    fun fromList_thenFromString_roundTripsPreservesOrderAndContent() {
        val original = listOf("Alice", "Bob", "Carol")

        val json = converters.fromList(original)
        val restored = converters.fromString(json)

        assertEquals(original, restored)
    }

    @Test
    fun fromList_emptyList_serializesToEmptyJsonArray() {
        assertEquals("[]", converters.fromList(emptyList()))
    }

    @Test
    fun fromString_emptyJsonArray_returnsEmptyList() {
        assertEquals(emptyList<String>(), converters.fromString("[]"))
    }

    @Test
    fun fromString_jsonArray_parsesAllElements() {
        assertEquals(listOf("Chess", "Catan"), converters.fromString("""["Chess","Catan"]"""))
    }

    // -------------------------------------------------------------------------
    // String -> LocalDate
    // -------------------------------------------------------------------------

    @Test
    fun fromStringToLocalDate_validIsoDate_parsesCorrectly() {
        assertEquals(LocalDate.of(2024, 3, 15), converters.fromStringToLocalDate("2024-03-15"))
    }

    @Test
    fun fromStringToLocalDate_null_fallsBackToSentinelDate() {
        assertEquals(LocalDate.of(1900, 1, 1), converters.fromStringToLocalDate(null))
    }

    @Test
    fun fromStringToLocalDate_blank_fallsBackToSentinelDate() {
        assertEquals(LocalDate.of(1900, 1, 1), converters.fromStringToLocalDate("   "))
    }

    @Test
    fun fromStringToLocalDate_zeroPlaceholder_fallsBackToSentinelDate() {
        assertEquals(LocalDate.of(1900, 1, 1), converters.fromStringToLocalDate("0000-00-00"))
    }

    @Test
    fun fromStringToLocalDate_malformedValue_returnsNull() {
        assertNull(converters.fromStringToLocalDate("not-a-date"))
    }

    // -------------------------------------------------------------------------
    // LocalDate -> String
    // -------------------------------------------------------------------------

    @Test
    fun fromLocalDateToString_validDate_formatsAsIsoString() {
        assertEquals("2024-03-15", converters.fromLocalDateToString(LocalDate.of(2024, 3, 15)))
    }

    @Test
    fun fromLocalDateToString_null_returnsNull() {
        assertNull(converters.fromLocalDateToString(null))
    }
}
