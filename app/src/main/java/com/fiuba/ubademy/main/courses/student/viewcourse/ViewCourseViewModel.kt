package com.fiuba.ubademy.main.courses.student.viewcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.network.model.GoogleData
import com.fiuba.ubademy.network.model.UnenrollCourseRequest
import com.fiuba.ubademy.network.model.User
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class ViewCourseViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var courseType = MutableLiveData<String>()
    var creator = MutableLiveData<String>()
    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String>()

    var getUserResponse = MutableLiveData<GetUserResponse>()
    var creatorDisplayName = MutableLiveData<String>()

    suspend fun unenrollCourse() : UnenrollCourseStatus {
        var unenrollCourseStatus = UnenrollCourseStatus.FAIL
        try {
            val response = api().unenrollCourse(
                UnenrollCourseRequest(
                    userId = getSharedPreferencesData().id,
                    courseId = courseId.value!!
                )
            )
            if (response.isSuccessful)
                unenrollCourseStatus = UnenrollCourseStatus.SUCCESS
        } catch (e: Exception) {
            Timber.e(e)
        }
        return unenrollCourseStatus
    }

    suspend fun getCreator() : GetCreatorStatus {
        var getCreatorStatus = GetCreatorStatus.FAIL
        try {
            getCreatorStatus = GetCreatorStatus.SUCCESS
            getUserResponse.value = GetUserResponse(
                user = User(
                    id = "618d8af045b7950011e71e2c",
                    email = "ndyatlov@sparkdigital.com",
                    interests = setOf("YOGA", "WOODWORKING"),
                    firstName = null,
                    lastName = null,
                    placeId = null
                ),
                googleData = GoogleData(
                    displayName = "Nikita Dyatlov",
                    picture = "https://lh3.googleusercontent.com/a-/AOh14GhVq6sHaKnlCjnliwcS6TxuRUwzUiZqy__rSybx=s96-c"
                )
            )
            creatorDisplayName.value = getUserResponse.value!!.displayName
            // TODO:
//            val response = api().getUser(creator.value!!)
//            if (response.isSuccessful) {
//                getUserResponse.value = response.body()
//                getCreatorStatus = GetCreatorStatus.SUCCESS
//            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return getCreatorStatus
    }
}