package com.fiuba.ubademy.main.profile.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.EditProfileRequest
import com.fiuba.ubademy.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    var editInProgress = MutableLiveData<Boolean>()

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String?>()
    var email = MutableLiveData<String>()

    private val userId : String

    init {
        editInProgress.value = false

        val sharedPreferencesData = getSharedPreferencesData()
        firstName.value = sharedPreferencesData.firstName
        lastName.value = sharedPreferencesData.lastName
        placeId.value = sharedPreferencesData.placeId
        placeName.value = sharedPreferencesData.placeName
        email.value = sharedPreferencesData.email
        userId = sharedPreferencesData.id
    }

    suspend fun editProfile() : EditProfileStatus {
        var editProfileStatus = EditProfileStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = usersApi().editProfile(
                    userId,
                    EditProfileRequest(
                        firstName = firstName.value!!,
                        lastName = lastName.value!!,
                        placeId = placeId.value,
                        email = email.value!!
                    )
                )
                if (response.isSuccessful) {
                    editProfileStatus = EditProfileStatus.SUCCESS
                    val sharedPreferencesData = getSharedPreferencesData()
                    setSharedPreferencesData(SharedPreferencesData(
                        id = sharedPreferencesData.id,
                        firstName = firstName.value!!,
                        lastName = lastName.value!!,
                        placeId = placeId.value,
                        placeName = placeName.value,
                        email = email.value!!,
                        token = sharedPreferencesData.token
                    ))
                } else if (response.code() == 409) {
                    editProfileStatus = EditProfileStatus.EMAIL_ALREADY_USED
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return editProfileStatus
    }
}