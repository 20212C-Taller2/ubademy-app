package com.fiuba.ubademy.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel : ViewModel() {

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    suspend fun login() {
        val job = viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            Timber.i("LOLOLO")
        }
        job.join()
    }
}