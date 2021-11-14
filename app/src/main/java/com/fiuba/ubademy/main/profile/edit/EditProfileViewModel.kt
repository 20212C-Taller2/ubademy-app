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

    var firstName = MutableLiveData<String?>()
    var lastName = MutableLiveData<String?>()
    var displayName = MutableLiveData<String?>()
    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String?>()
    var email = MutableLiveData<String>()

    var courseTypes = MutableLiveData<Array<String>>()
    var selectedCourseTypes = MutableLiveData<BooleanArray>()
    var selectedCourseTypesText = MutableLiveData<String>()

    private val sharedPreferencesData : SharedPreferencesData = getSharedPreferencesData()
    private val userId : String

    init {
        editInProgress.value = false

        firstName.value = sharedPreferencesData.firstName
        lastName.value = sharedPreferencesData.lastName
        displayName.value = sharedPreferencesData.displayName
        placeId.value = sharedPreferencesData.placeId
        placeName.value = sharedPreferencesData.placeName
        email.value = sharedPreferencesData.email
        userId = sharedPreferencesData.id

        courseTypes.value = arrayOf()
        selectedCourseTypes.value = booleanArrayOf()
    }

    suspend fun getCourseTypes() {
        val response = coursesApi().getCourseTypes()
        if (response.isSuccessful) {
            courseTypes.value = response.body()!!.toTypedArray()
            selectedCourseTypes.value = response.body()!!.map { item -> sharedPreferencesData.interests.contains(item) }.toBooleanArray()
        } else {
            throw Exception("Unable to fetch course types.")
        }
    }

    suspend fun editProfile() : EditProfileStatus {
        var editProfileStatus = EditProfileStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = usersApi().editProfile(
                    userId,
                    EditProfileRequest(
                        firstName = firstName.value,
                        lastName = lastName.value,
                        placeId = placeId.value,
                        email = if (sharedPreferencesData.loggedInWithGoogle) null else email.value,
                        interests = courseTypes.value!!.filterIndexed { index, _ -> selectedCourseTypes.value!![index] }.toSet()
                    )
                )
                if (response.isSuccessful) {
                    editProfileStatus = EditProfileStatus.SUCCESS
                    val sharedPreferencesData = getSharedPreferencesData()
                    setSharedPreferencesData(SharedPreferencesData(
                        id = sharedPreferencesData.id,
                        firstName = firstName.value,
                        lastName = lastName.value,
                        placeId = placeId.value,
                        placeName = placeName.value,
                        email = email.value!!,
                        token = sharedPreferencesData.token,
                        loggedInWithGoogle = sharedPreferencesData.loggedInWithGoogle,
                        displayName = sharedPreferencesData.displayName,
                        picture = sharedPreferencesData.picture,
                        interests = courseTypes.value!!.filterIndexed { index, _ -> selectedCourseTypes.value!![index] }.toSet()
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