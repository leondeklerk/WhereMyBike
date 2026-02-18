package com.leondeklerk.wheremybike.di

import com.leondeklerk.wheremybike.data.db.DatabaseDriverFactory
import com.leondeklerk.wheremybike.data.db.createDatabase
import com.leondeklerk.wheremybike.data.repository.BikeLocationRepository
import com.leondeklerk.wheremybike.data.repository.BikeLocationRepositoryImpl
import com.leondeklerk.wheremybike.data.repository.ConfigRepository
import com.leondeklerk.wheremybike.data.repository.ConfigRepositoryImpl
import com.leondeklerk.wheremybike.ui.screens.home.HomeViewModel
import com.leondeklerk.wheremybike.ui.screens.maps.MapsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    // Database
    single { createDatabase(get()) }

    // Repositories
    singleOf(::BikeLocationRepositoryImpl) bind BikeLocationRepository::class
    singleOf(::ConfigRepositoryImpl) bind ConfigRepository::class

    // ViewModels
    factory { HomeViewModel(get(), get()) }
    factory { MapsViewModel(get()) }
}


