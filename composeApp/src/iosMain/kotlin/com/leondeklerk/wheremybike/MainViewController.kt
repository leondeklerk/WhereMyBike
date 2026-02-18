package com.leondeklerk.wheremybike

import androidx.compose.ui.window.ComposeUIViewController
import com.leondeklerk.wheremybike.di.iosModule
import com.leondeklerk.wheremybike.di.sharedModule
import com.leondeklerk.wheremybike.ui.WhereMyBikeApp
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController

fun MainViewController(isDebug: Boolean = false): UIViewController {
    initKoin()
    return ComposeUIViewController { WhereMyBikeApp(isDebug = isDebug) }
}

fun initKoin() {
    try {
        KoinPlatform.getKoin()
    } catch (_: Exception) {
        startKoin {
            modules(sharedModule, iosModule)
        }
    }
}


