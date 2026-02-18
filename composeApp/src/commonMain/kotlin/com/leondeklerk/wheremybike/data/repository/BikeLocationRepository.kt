@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.data.repository

import com.leondeklerk.wheremybike.data.model.BikeLocation
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime

interface BikeLocationRepository {
    fun observeCurrentLocation(): Flow<BikeLocation?>
    fun observeLocationHistory(): Flow<List<BikeLocation>>
    suspend fun insertLocation(location: BikeLocation)
}

