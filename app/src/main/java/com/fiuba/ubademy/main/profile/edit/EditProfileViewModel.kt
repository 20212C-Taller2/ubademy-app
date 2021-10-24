package com.fiuba.ubademy.main.profile.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiuba.ubademy.network.model.EditProfileRequest
import com.fiuba.ubademy.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var placeId = MutableLiveData<String>()
    var placeName = MutableLiveData<String>()
    var email = MutableLiveData<String>()

    private val userId : String

    init {
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

        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api().editProfile(
                    userId,
                    EditProfileRequest(
                        firstName = firstName.value.toString(),
                        lastName = lastName.value.toString(),
                        placeId = placeId.value.toString(),
                        email = email.value.toString()
                    )
                )
                if (response.isSuccessful) {
                    editProfileStatus = EditProfileStatus.SUCCESS
                    val sharedPreferencesData = getSharedPreferencesData()
                    setSharedPreferencesData(SharedPreferencesData(
                        id = sharedPreferencesData.id,
                        firstName = firstName.value.toString(),
                        lastName = lastName.value.toString(),
                        placeId = placeId.value.toString(),
                        placeName = placeName.value.toString(),
                        email = email.value.toString(),
                        token = sharedPreferencesData.token
                    ))
                } else {
                    editProfileStatus = EditProfileStatus.EMAIL_ALREADY_USED
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        job.join()

        return editProfileStatus
    }
}