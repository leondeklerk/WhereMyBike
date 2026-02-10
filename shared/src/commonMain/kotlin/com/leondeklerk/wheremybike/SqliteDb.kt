package com.leondeklerk.wheremybike

import app.cash.sqldelight.db.SqlDriver
import com.leondeklerk.wheremybike.shared.db.Database
import kotlinx.datetime.Instant

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(driver);
}

fun Database.insertLocation(location: ManualLocationEntry) {
    manualLocationEntryQueries.insert(
        location.startDate.toEpochMilliseconds(),
        location.expiredDate.toEpochMilliseconds(),
        location.location
    )
}

fun Database.getCurrentLocation(): ManualLocationEntry? {
    return try {
        manualLocationEntryQueries.selectLatest().executeAsOne().let {
            ManualLocationEntry(
                Instant.fromEpochMilliseconds(it.start_date),
                Instant.fromEpochMilliseconds(it.expire_date),
                it.location
            )
        }
    } catch (e: NullPointerException) {
        null
    }
}

fun Database.getHistory(): List<ManualLocationEntry> {
    return manualLocationEntryQueries.selectAll().executeAsList().map {
        ManualLocationEntry(
            Instant.fromEpochMilliseconds(it.start_date),
            Instant.fromEpochMilliseconds(it.expire_date),
            it.location
        )
    }
}

fun Database.getConfig(key: String): String? {
    return configQueries.getConfig(key).executeAsOneOrNull()?.value_
}

fun Database.setConfig(key: String, value: String?) {
    configQueries.setConfig(key, value)
}
