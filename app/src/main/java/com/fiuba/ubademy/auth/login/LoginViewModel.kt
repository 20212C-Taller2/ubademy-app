package com.fiuba.ubademy.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.LoginRequest
import com.fiuba.ubademy.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    suspend fun login() : LoginStatus {
        var loginStatus = LoginStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = api().login(
                    LoginRequest(
                        email = email.value.toString(),
                        password = password.value.toString()
                    )
                )
                if (response.isSuccessful) {
                    loginStatus = LoginStatus.SUCCESS
                    val place = getPlaceById(response.body()!!.user.placeId)
                    setSharedPreferencesData(SharedPreferencesData(
                        id = response.body()!!.user.id,
                        firstName = response.body()!!.user.firstName,
                        lastName = response.body()!!.user.lastName,
                        email = response.body()!!.user.email,
                        placeId = response.body()!!.user.placeId,
                        placeName = place?.address ?: "-",
                        token = response.body()!!.token
                    ))
                } else {
                    loginStatus = LoginStatus.INVALID_CREDENTIALS
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return loginStatus
    }
}