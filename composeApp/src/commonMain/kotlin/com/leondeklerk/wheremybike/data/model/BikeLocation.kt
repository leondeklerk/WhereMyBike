@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.data.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class BikeLocation(
    val startDate: Instant,
    val expiredDate: Instant,
    val location: String
) {
    val isExpired: Boolean
        get() = expiredDate <= kotlin.time.Clock.System.now()
}

