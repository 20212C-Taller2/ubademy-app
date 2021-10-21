package com.fiuba.ubademy.auth.createaccount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiuba.ubademy.network.model.CreateAccountRequest
import com.fiuba.ubademy.utils.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateAccountViewModel(application: Application) : AndroidViewModel(application) {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    suspend fun createAccount() : CreateAccountStatus {
        var createAccountStatus = CreateAccountStatus.FAIL

        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api().createAccount(
                    CreateAccountRequest(
                        firstName = firstName.value.toString(),
                        lastName = lastName.value.toString(),
                        email = email.value.toString(),
                        password = password.value.toString()
                    )
                )
                createAccountStatus = if (response.isSuccessful) {
                    CreateAccountStatus.SUCCESS
                } else {
                    CreateAccountStatus.EMAIL_ALREADY_USED
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        job.join()

        return createAccountStatus
    }
}