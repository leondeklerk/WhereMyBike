@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike

import kotlin.time.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSince1970
import kotlin.time.ExperimentalTime

actual fun Instant.formatDate(pattern: String, defValue: String): String {
    return try {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = pattern
        dateFormatter.stringFromDate(
            toNSDate()
        )
    } catch (e: Exception) {
        defValue
    }

}

actual fun String.parseDate(pattern: String, defValue: Long): Long {
    return try {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = pattern
        val date = dateFormatter.dateFromString(this)
        if (date != null) {
            (date.timeIntervalSince1970 * 1000).toLong()
        } else {
            defValue
        }
    } catch (e: Exception) {
        defValue
    }
}
