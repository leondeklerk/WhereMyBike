@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class ManualLocationEntry(
    val startDate: Instant,
    val expiredDate: Instant,
    val location: String
)
