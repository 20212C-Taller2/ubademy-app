package com.fiuba.ubademy.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.LoginRequest
import com.fiuba.ubademy.network.model.LoginWithGoogleRequest
import com.fiuba.ubademy.network.model.UpdateFcmTokenRequest
import com.fiuba.ubademy.utils.*
import com.google.android.libraries.places.api.model.Place
import timber.log.Timber

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    val loggedUserId = MutableLiveData("")

    suspend fun login() : LoginStatus {
        var loginStatus = LoginStatus.FAIL

        try {
            val response = api().login(
                LoginRequest(
                    email = email.value!!,
                    password = password.value!!
                )
            )
            if (response.isSuccessful) {
                loginStatus = LoginStatus.SUCCESS
                loggedUserId.value = response.body()!!.user.id
                var place : Place? = null
                if (!response.body()!!.user.placeId.isNullOrBlank())
                    place = getPlaceById(response.body()!!.user.placeId!!)
                setSharedPreferencesData(SharedPreferencesData(
                    id = response.body()!!.user.id,
                    firstName = response.body()!!.user.firstName,
                    lastName = response.body()!!.user.lastName,
                    email = response.body()!!.user.email,
                    placeId = place?.id,
                    placeName = place?.address,
                    token = response.body()!!.token,
                    loggedInWithGoogle = false,
                    displayName = null,
                    picture = null,
                    interests = response.body()!!.user.interests
                ))
            } else {
                val error = response.errorBody()?.string()
                loginStatus = if (error?.contains("incorrect", true) == true)
                    LoginStatus.INVALID_CREDENTIALS
                else if (error?.contains("blocked", true) == true)
                    LoginStatus.USER_BLOCKED
                else
                    LoginStatus.FAIL
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return loginStatus
    }

    suspend fun loginWithGoogle(idToken: String) : LoginStatus {
        var loginStatus = LoginStatus.FAIL

        try {
            val response = api().loginWithGoogle(
                LoginWithGoogleRequest(
                    googleToken = idToken
                )
            )
            if (response.isSuccessful) {
                loginStatus = LoginStatus.SUCCESS
                loggedUserId.value = response.body()!!.user.id
                var place : Place? = null
                if (!response.body()!!.user.placeId.isNullOrBlank())
                    place = getPlaceById(response.body()!!.user.placeId!!)
                setSharedPreferencesData(SharedPreferencesData(
                    id = response.body()!!.user.id,
                    firstName = null,
                    lastName = null,
                    email = response.body()!!.user.email,
                    placeId = place?.id,
                    placeName = place?.address,
                    token = response.body()!!.token,
                    loggedInWithGoogle = true,
                    displayName = response.body()!!.googleData!!.displayName,
                    picture = response.body()!!.googleData!!.picture,
                    interests = response.body()!!.user.interests
                ))
            } else {
                val error = response.errorBody()?.string()
                loginStatus = if (error?.contains("incorrect", true) == true)
                    LoginStatus.INVALID_CREDENTIALS
                else if (error?.contains("blocked", true) == true)
                    LoginStatus.USER_BLOCKED
                else
                    LoginStatus.FAIL
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return loginStatus
    }

    suspend fun updateFcmToken() {
        try {
            val fcmtoken = getFcmToken()
            if (fcmtoken != null)
                api().updateFcmToken(loggedUserId.value!!, UpdateFcmTokenRequest(fcmtoken))
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}