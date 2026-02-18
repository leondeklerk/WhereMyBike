package com.leondeklerk.wheremybike.android

import android.app.Application
import com.leondeklerk.wheremybike.di.androidModule
import com.leondeklerk.wheremybike.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WhereMyBikeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@WhereMyBikeApplication)
            modules(sharedModule, androidModule)
        }
    }
}

