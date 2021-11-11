package com.fiuba.ubademy.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.utils.getDisplayName
import com.fiuba.ubademy.utils.getSharedPreferencesData

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var displayName = MutableLiveData<String>()

    init {
        refreshFromSharedPreferences()
    }

    fun refreshFromSharedPreferences() {
        displayName.value = getSharedPreferencesData().getDisplayName()
    }
}