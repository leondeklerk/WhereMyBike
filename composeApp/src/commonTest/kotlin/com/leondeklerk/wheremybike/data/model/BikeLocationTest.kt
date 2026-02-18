@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.data.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.Duration.Companion.days

class BikeLocationTest {

    @Test
    fun isExpired_whenExpiredDateInPast_returnsTrue() {
        val location = BikeLocation(
            startDate = Clock.System.now() - 30.days,
            expiredDate = Clock.System.now() - 1.days,
            location = "1-2-3"
        )
        assertTrue(location.isExpired)
    }

    @Test
    fun isExpired_whenExpiredDateInFuture_returnsFalse() {
        val location = BikeLocation(
            startDate = Clock.System.now(),
            expiredDate = Clock.System.now() + 14.days,
            location = "1-2-3"
        )
        assertFalse(location.isExpired)
    }

    @Test
    fun locationString_isPreserved() {
        val location = BikeLocation(
            startDate = Instant.fromEpochMilliseconds(1000),
            expiredDate = Instant.fromEpochMilliseconds(2000),
            location = "A-B-C"
        )
        kotlin.test.assertEquals("A-B-C", location.location)
    }
}
