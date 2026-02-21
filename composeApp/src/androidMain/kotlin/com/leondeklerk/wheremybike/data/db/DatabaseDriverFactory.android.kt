package com.leondeklerk.wheremybike.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.leondeklerk.wheremybike.db.Database

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        migrateOldDatabase()
        return AndroidSqliteDriver(Database.Schema, context, DB_NAME)
    }

    private fun migrateOldDatabase() {
        val oldDbFile = context.getDatabasePath(OLD_DB_NAME)
        val newDbFile = context.getDatabasePath(DB_NAME)
        if (oldDbFile.exists() && !newDbFile.exists()) {
            oldDbFile.renameTo(newDbFile)
            // Also migrate the journal/wal files if present
            listOf("-journal", "-wal", "-shm").forEach { suffix ->
                val oldFile = context.getDatabasePath("$OLD_DB_NAME$suffix")
                val newFile = context.getDatabasePath("$DB_NAME$suffix")
                if (oldFile.exists()) oldFile.renameTo(newFile)
            }
        }
    }
}

