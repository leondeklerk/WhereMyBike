@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Date format: dd-MM-yyyy
 */
private val dateOnlyFormat = LocalDateTime.Format {
    day()
    char('-')
    monthNumber()
    char('-')
    year()
}

/**
 * Date format: MM-dd-yyyy HH:mm
 */
private val dateTimeFormat = LocalDateTime.Format {
    monthNumber()
    char('-')
    day()
    char('-')
    year()
    char(' ')
    hour()
    char(':')
    minute()
}

private val formatMap = mapOf(
    "dd-MM-yyyy" to dateOnlyFormat,
    "MM-dd-yyyy HH:mm" to dateTimeFormat
)

fun Instant.formatDate(pattern: String, defValue: String = ""): String {
    return try {
        val format = formatMap[pattern] ?: return defValue
        val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
        format.format(localDateTime)
    } catch (e: Exception) {
        defValue
    }
}

