package com.fiuba.ubademy.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class LoginViewModel : ViewModel() {

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    fun login() {
        Timber.i("email: ${email.value}")
        Timber.i("password: ${password.value}")
    }
}