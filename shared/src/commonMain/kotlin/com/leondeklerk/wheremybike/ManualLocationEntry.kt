package com.leondeklerk.wheremybike

import kotlinx.datetime.Instant

data class ManualLocationEntry(val startDate: Instant, val expiredDate: Instant, val location: String)
