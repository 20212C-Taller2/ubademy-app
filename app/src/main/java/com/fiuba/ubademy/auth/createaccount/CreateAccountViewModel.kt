package com.fiuba.ubademy.auth.createaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateAccountViewModel : ViewModel() {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    suspend fun submitAccountCreatedForm() {
        val job = viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            Timber.i("PIPIPI")
        }
        job.join()
    }
}