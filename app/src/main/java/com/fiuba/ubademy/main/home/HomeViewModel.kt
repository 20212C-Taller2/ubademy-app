package com.fiuba.ubademy.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.utils.getSharedPreferencesData

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var placeName = MutableLiveData<String>()

    init {
        val sharedPreferencesData = getSharedPreferencesData()
        firstName.value = sharedPreferencesData.firstName
        lastName.value = sharedPreferencesData.lastName
        placeName.value = sharedPreferencesData.placeName
    }
}