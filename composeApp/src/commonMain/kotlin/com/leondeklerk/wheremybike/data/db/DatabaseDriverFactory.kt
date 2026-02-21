package com.leondeklerk.wheremybike.data.db

import app.cash.sqldelight.db.SqlDriver
import com.leondeklerk.wheremybike.db.Database

// old DB was used in 2.0.3 and older by mistake so only applicable to android
const val OLD_DB_NAME = "test.db"
const val DB_NAME = "wheremybike.db"

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(driver)
}

