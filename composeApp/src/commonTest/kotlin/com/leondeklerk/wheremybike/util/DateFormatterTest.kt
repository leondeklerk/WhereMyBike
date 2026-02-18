@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class DateFormatterTest {

    @Test
    fun formatDateOnly_returnsCorrectFormat() {
        // 2025-03-15T10:30:00Z = March 15, 2025 — dd-MM-yyyy
        val instant = Instant.fromEpochMilliseconds(1742033400000L) // 2025-03-15T10:30:00Z
        val result = instant.formatDate("dd-MM-yyyy")
        // The exact output depends on system timezone, but the format should be dd-MM-yyyy
        assertEquals(10, result.length, "Date-only format should be 10 characters (dd-MM-yyyy)")
        assertEquals('-', result[2])
        assertEquals('-', result[5])
    }

    @Test
    fun formatDateTime_returnsCorrectFormat() {
        val instant = Instant.fromEpochMilliseconds(1742033400000L) // 2025-03-15T10:30:00Z
        val result = instant.formatDate("MM-dd-yyyy HH:mm")
        // Format: MM-dd-yyyy HH:mm = 16 chars
        assertEquals(16, result.length, "DateTime format should be 16 characters (MM-dd-yyyy HH:mm)")
        assertEquals('-', result[2])
        assertEquals('-', result[5])
        assertEquals(' ', result[10])
        assertEquals(':', result[13])
    }

    @Test
    fun formatDate_unknownPattern_returnsDefaultValue() {
        val instant = Instant.fromEpochMilliseconds(1742033400000L)
        val result = instant.formatDate("yyyy/MM/dd", "N/A")
        assertEquals("N/A", result)
    }

    @Test
    fun formatDate_unknownPattern_returnsEmptyStringByDefault() {
        val instant = Instant.fromEpochMilliseconds(1742033400000L)
        val result = instant.formatDate("unknown-pattern")
        assertEquals("", result)
    }
}
