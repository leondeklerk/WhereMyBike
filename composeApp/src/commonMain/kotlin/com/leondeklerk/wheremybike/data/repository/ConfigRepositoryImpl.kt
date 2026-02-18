package com.leondeklerk.wheremybike.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.leondeklerk.wheremybike.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConfigRepositoryImpl(
    private val database: Database
) : ConfigRepository {

    override fun observeConfig(key: String): Flow<String?> {
        return database.configQueries.getConfig(key)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.value_ }
    }

    override suspend fun setConfig(key: String, value: String?) {
        database.configQueries.setConfig(key, value)
    }
}

