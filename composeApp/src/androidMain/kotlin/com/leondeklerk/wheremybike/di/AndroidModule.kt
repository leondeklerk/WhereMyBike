package com.leondeklerk.wheremybike.di

import com.leondeklerk.wheremybike.data.db.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
}

