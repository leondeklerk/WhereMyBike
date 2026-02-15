package com.leondeklerk.wheremybike

import kotlin.time.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun Instant.formatDate(pattern: String, defValue: String): String {
    return try {
        SimpleDateFormat(pattern, Locale.ENGLISH).format(Date(this.toEpochMilliseconds()))
    } catch (e: Exception) {
        defValue
    }
}

actual fun String.parseDate(pattern: String, defValue: Long): Long {
    return try {
        SimpleDateFormat(pattern, Locale.ENGLISH).parse(this)!!.time
    } catch (e: Exception) {
        defValue
    }
}
