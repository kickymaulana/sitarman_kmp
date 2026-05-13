package com.gotechdynamics.sitarman

import android.app.Application
import com.gotechdynamics.sitarman.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class SitarmanApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@SitarmanApp)
        }
    }
}