@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.leondeklerk.wheremybike.data.model.BikeLocation
import com.leondeklerk.wheremybike.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class BikeLocationRepositoryImpl(
    private val database: Database
) : BikeLocationRepository {

    override fun observeCurrentLocation(): Flow<BikeLocation?> {
        return database.manualLocationEntryQueries.selectLatest()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { row ->
                row?.let {
                    BikeLocation(
                        Instant.fromEpochMilliseconds(it.start_date),
                        Instant.fromEpochMilliseconds(it.expire_date),
                        it.location
                    )
                }
            }
    }

    override fun observeLocationHistory(): Flow<List<BikeLocation>> {
        return database.manualLocationEntryQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.map {
                    BikeLocation(
                        Instant.fromEpochMilliseconds(it.start_date),
                        Instant.fromEpochMilliseconds(it.expire_date),
                        it.location
                    )
                }
            }
    }

    override suspend fun insertLocation(location: BikeLocation) {
        database.manualLocationEntryQueries.insert(
            location.startDate.toEpochMilliseconds(),
            location.expiredDate.toEpochMilliseconds(),
            location.location
        )
    }
}

