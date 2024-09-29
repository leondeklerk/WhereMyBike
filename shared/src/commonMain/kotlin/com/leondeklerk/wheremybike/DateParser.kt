package com.leondeklerk.wheremybike

import kotlinx.datetime.Instant

expect fun Instant.formatDate(pattern: String, defValue: String = ""): String

expect fun String.parseDate(pattern: String, defValue: Long = 0L): Long

fun Long.formatDate(pattern: String, defValue: String = ""): String {
    return Instant.fromEpochMilliseconds(this).formatDate(pattern, defValue)
}
