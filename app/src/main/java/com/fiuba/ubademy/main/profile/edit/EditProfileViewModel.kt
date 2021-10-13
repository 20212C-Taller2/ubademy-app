package com.fiuba.ubademy.main.profile.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiuba.ubademy.network.UbademyApiService
import com.fiuba.ubademy.network.model.EditProfileRequest
import com.fiuba.ubademy.utils.getSharedPreferencesData
import com.fiuba.ubademy.utils.updateSharedPreferencesEmail
import com.fiuba.ubademy.utils.updateSharedPreferencesFirstName
import com.fiuba.ubademy.utils.updateSharedPreferencesLastName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var email = MutableLiveData<String>()

    private val userId : String
    private val jwtToken : String

    init {
        val sharedPreferencesData = getSharedPreferencesData()
        firstName.value = sharedPreferencesData.firstName
        lastName.value = sharedPreferencesData.lastName
        email.value = sharedPreferencesData.email
        userId = sharedPreferencesData.id
        jwtToken = "Bearer ${sharedPreferencesData.token}"
    }

    suspend fun editProfile() : EditProfileStatus {
        var editProfileStatus = EditProfileStatus.FAIL

        val job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = UbademyApiService.UbademyApi.retrofitService.editProfile(
                    userId,
                    EditProfileRequest(
                        firstName = firstName.value.toString(),
                        lastName = lastName.value.toString(),
                        email = email.value.toString()
                    ),
                    jwtToken
                )
                if (response.isSuccessful) {
                    editProfileStatus = EditProfileStatus.SUCCESS
                    updateSharedPreferencesFirstName(firstName.value.toString())
                    updateSharedPreferencesLastName(lastName.value.toString())
                    updateSharedPreferencesEmail(email.value.toString())
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