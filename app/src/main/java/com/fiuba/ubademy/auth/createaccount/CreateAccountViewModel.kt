package com.fiuba.ubademy.auth.createaccount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiuba.ubademy.network.UbademyApiService
import com.fiuba.ubademy.network.model.CreateAccountRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateAccountViewModel(application: Application) : AndroidViewModel(application) {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    suspend fun createAccount() : Boolean {
        var success = false

        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = UbademyApiService.UbademyApi.retrofitService.createAccount(
                    CreateAccountRequest(
                        firstName = firstName.value.toString(),
                        lastName = lastName.value.toString(),
                        email = email.value.toString(),
                        password = password.value.toString()
                    )
                )
                success = response.body()?.auth ?: false
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        job.join()

        return success
    }
}