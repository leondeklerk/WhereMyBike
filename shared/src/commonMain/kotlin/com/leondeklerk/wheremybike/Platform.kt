package com.leondeklerk.wheremybike

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
