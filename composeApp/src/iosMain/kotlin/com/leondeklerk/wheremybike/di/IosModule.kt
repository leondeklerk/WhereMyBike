package com.leondeklerk.wheremybike.di

import com.leondeklerk.wheremybike.data.db.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
}

