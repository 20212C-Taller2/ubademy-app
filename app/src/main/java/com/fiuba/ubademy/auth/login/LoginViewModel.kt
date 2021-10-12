package com.fiuba.ubademy.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiuba.ubademy.network.UbademyApiService
import com.fiuba.ubademy.network.model.LoginRequest
import com.fiuba.ubademy.utils.SharedPreferencesData
import com.fiuba.ubademy.utils.addSharedPreferencesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    suspend fun login() : LoginStatus {
        var loginStatus = LoginStatus.FAIL

        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = UbademyApiService.UbademyApi.retrofitService.login(
                    LoginRequest(
                        email = email.value.toString(),
                        password = password.value.toString()
                    )
                )
                if (response.isSuccessful) {
                    loginStatus = LoginStatus.SUCCESS
                    addSharedPreferencesData(SharedPreferencesData(
                        id = response.body()!!.user.id,
                        firstName = response.body()!!.user.firstName,
                        lastName = response.body()!!.user.lastName,
                        email = response.body()!!.user.email,
                        token = response.body()!!.token
                    ))
                } else {
                    loginStatus = LoginStatus.INVALID_CREDENTIALS
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        job.join()

        return loginStatus
    }
}