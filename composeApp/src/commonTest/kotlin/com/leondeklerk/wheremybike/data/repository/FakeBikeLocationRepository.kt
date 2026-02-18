@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.data.repository

import com.leondeklerk.wheremybike.data.model.BikeLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

class FakeBikeLocationRepository : BikeLocationRepository {

    private val _locations = MutableStateFlow<List<BikeLocation>>(emptyList())

    override fun observeCurrentLocation(): Flow<BikeLocation?> {
        return _locations.map { it.firstOrNull() }
    }

    override fun observeLocationHistory(): Flow<List<BikeLocation>> {
        return _locations
    }

    override suspend fun insertLocation(location: BikeLocation) {
        _locations.value = listOf(location) + _locations.value
    }

    fun setLocations(locations: List<BikeLocation>) {
        _locations.value = locations
    }
}
