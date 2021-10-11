package com.fiuba.ubademy.auth.createaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiuba.ubademy.network.UbademyApiService
import com.fiuba.ubademy.network.model.CreateAccountRequest
import com.fiuba.ubademy.network.model.CreateAccountResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateAccountViewModel : ViewModel() {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private val _createAccountResponse = MutableLiveData<CreateAccountResponse>()

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
                _createAccountResponse.postValue(response.body())
                success = true
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        job.join()
        Timber.i("Account created! Token: ${_createAccountResponse.value?.token}")
        return success
    }
}