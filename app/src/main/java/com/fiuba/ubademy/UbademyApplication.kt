package com.fiuba.ubademy

import android.app.Application
import com.google.android.libraries.places.api.Places
import timber.log.Timber

class UbademyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Places.initialize(applicationContext, getString(R.string.ubademy_api_key))
    }
}