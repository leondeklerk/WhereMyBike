package com.leondeklerk.wheremybike.data.db

import app.cash.sqldelight.db.SqlDriver
import com.leondeklerk.wheremybike.db.Database

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(driver)
}

