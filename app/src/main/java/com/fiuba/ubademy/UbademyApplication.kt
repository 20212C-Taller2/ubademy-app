package com.fiuba.ubademy

import android.app.Application
import timber.log.Timber

class UbademyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}