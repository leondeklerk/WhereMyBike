package com.leondeklerk.wheremybike.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeConfigRepository : ConfigRepository {

    private val _configs = MutableStateFlow<Map<String, String?>>(
        mapOf(
            "default_expire_days" to "28",
            "maps_location" to null
        )
    )

    override fun observeConfig(key: String): Flow<String?> {
        return _configs.map { it[key] }
    }

    override suspend fun setConfig(key: String, value: String?) {
        _configs.value = _configs.value.toMutableMap().apply {
            put(key, value)
        }
    }

    fun getConfigValue(key: String): String? {
        return _configs.value[key]
    }
}
