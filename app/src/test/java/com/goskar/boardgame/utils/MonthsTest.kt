package com.goskar.boardgame.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MonthsTest {

    @Test
    fun getMonthByNumber_validMonths_returnCorrectEntry() {
        assertEquals(Months.JANUARY,   Months.getMonthByNumber(1))
        assertEquals(Months.FEBRUARY,  Months.getMonthByNumber(2))
        assertEquals(Months.MARCH,     Months.getMonthByNumber(3))
        assertEquals(Months.APRIL,     Months.getMonthByNumber(4))
        assertEquals(Months.MAY,       Months.getMonthByNumber(5))
        assertEquals(Months.JUNE,      Months.getMonthByNumber(6))
        assertEquals(Months.JULY,      Months.getMonthByNumber(7))
        assertEquals(Months.AUGUST,    Months.getMonthByNumber(8))
        assertEquals(Months.SEPTEMBER, Months.getMonthByNumber(9))
        assertEquals(Months.OCTOBER,   Months.getMonthByNumber(10))
        assertEquals(Months.NOVEMBER,  Months.getMonthByNumber(11))
        assertEquals(Months.DECEMBER,  Months.getMonthByNumber(12))
    }

    @Test
    fun getMonthByNumber_zero_returnsNull() {
        assertNull(Months.getMonthByNumber(0))
    }

    @Test
    fun getMonthByNumber_aboveRange_returnsNull() {
        assertNull(Months.getMonthByNumber(13))
    }

    @Test
    fun getMonthByNumber_negative_returnsNull() {
        assertNull(Months.getMonthByNumber(-1))
    }

    @Test
    fun allMonthNumbers_areUnique() {
        val numbers = Months.entries.map { it.monthsNumber }
        assertEquals(numbers.distinct().size, numbers.size)
    }
}
