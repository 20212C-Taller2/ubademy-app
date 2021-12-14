package com.fiuba.ubademy.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.utils.getDisplayName
import com.fiuba.ubademy.utils.getSharedPreferencesData

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var displayName = MutableLiveData<String>()
    var picture = MutableLiveData<String?>()
    var placeName = MutableLiveData<String>()
    var email = MutableLiveData<String>()

    init {
        refreshFromSharedPreferences()
    }

    fun refreshFromSharedPreferences() {
        val sharedPreferencesData = getSharedPreferencesData()
        displayName.value = sharedPreferencesData.getDisplayName()
        picture.value = sharedPreferencesData.picture
        placeName.value = if (sharedPreferencesData.placeName.isNullOrBlank()) "-" else sharedPreferencesData.placeName
        email.value = sharedPreferencesData.email
    }
}