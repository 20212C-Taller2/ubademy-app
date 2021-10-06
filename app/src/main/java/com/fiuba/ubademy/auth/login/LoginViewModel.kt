package com.fiuba.ubademy.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class LoginViewModel : ViewModel() {

    var username = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    fun login() {
        Timber.i("username: ${username.value}")
        Timber.i("password: ${password.value}")
    }
}