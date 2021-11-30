package com.fiuba.ubademy.auth.createaccount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.CreateAccountRequest
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class CreateAccountViewModel(application: Application) : AndroidViewModel(application) {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String?>()
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    var courseTypes = MutableLiveData<Array<String>>()
    var selectedCourseTypes = MutableLiveData<BooleanArray>()
    var selectedCourseTypesText = MutableLiveData<String>()

    init {
        courseTypes.value = arrayOf()
        selectedCourseTypes.value = booleanArrayOf()
    }

    suspend fun getCourseTypes() {
        val response = api().getCourseTypes()
        if (response.isSuccessful) {
            courseTypes.value = response.body()!!.toTypedArray()
            selectedCourseTypes.value = response.body()!!.map { false }.toBooleanArray()
        } else {
            throw Exception("Unable to fetch course types.")
        }
    }

    suspend fun createAccount() : CreateAccountStatus {
        var createAccountStatus = CreateAccountStatus.FAIL

        try {
            val response = api().createAccount(
                CreateAccountRequest(
                    firstName = firstName.value!!,
                    lastName = lastName.value!!,
                    placeId = placeId.value,
                    email = email.value!!,
                    password = password.value!!,
                    interests = courseTypes.value!!.filterIndexed { index, _ -> selectedCourseTypes.value!![index] }.toSet()
                )
            )
            if (response.isSuccessful)
                createAccountStatus = CreateAccountStatus.SUCCESS
            else if (response.code() == 409)
                createAccountStatus = CreateAccountStatus.EMAIL_ALREADY_USED
        } catch (e: Exception) {
            Timber.e(e)
        }

        return createAccountStatus
    }
}