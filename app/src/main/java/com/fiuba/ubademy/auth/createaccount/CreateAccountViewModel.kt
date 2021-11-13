package com.fiuba.ubademy.auth.createaccount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.CreateAccountRequest
import com.fiuba.ubademy.utils.coursesApi
import com.fiuba.ubademy.utils.usersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        val response = coursesApi().getCourseTypes()
        if (response.isSuccessful) {
            courseTypes.postValue(response.body()!!.toTypedArray())
            selectedCourseTypes.postValue(response.body()!!.map { _ -> false }.toBooleanArray())
        } else {
            throw Exception("Unable to fetch course types.")
        }
    }

    suspend fun createAccount() : CreateAccountStatus {
        var createAccountStatus = CreateAccountStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = usersApi().createAccount(
                    CreateAccountRequest(
                        firstName = firstName.value!!,
                        lastName = lastName.value!!,
                        placeId = placeId.value,
                        email = email.value!!,
                        password = password.value!!
                    )
                )
                if (response.isSuccessful)
                    createAccountStatus = CreateAccountStatus.SUCCESS
                else if (response.code() == 409)
                    createAccountStatus = CreateAccountStatus.EMAIL_ALREADY_USED
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return createAccountStatus
    }
}