package com.fiuba.ubademy.main.profile.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()

    suspend fun saveChanges() {
        val job = viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            Timber.i("PIPIPI")
        }
        job.join()
    }
}