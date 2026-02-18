package com.leondeklerk.wheremybike.data.repository

import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun observeConfig(key: String): Flow<String?>
    suspend fun setConfig(key: String, value: String?)
}

