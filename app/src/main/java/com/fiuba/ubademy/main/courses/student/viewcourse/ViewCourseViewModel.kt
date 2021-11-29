package com.fiuba.ubademy.main.courses.student.viewcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.GetUserResponse
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

    suspend fun unenrollStudent() : UnenrollStudentStatus {
        var unenrollStudentStatus = UnenrollStudentStatus.FAIL
        try {
            val response = api().unenrollStudent(courseId.value!!, getSharedPreferencesData().id)
            if (response.isSuccessful)
                unenrollStudentStatus = UnenrollStudentStatus.SUCCESS
        } catch (e: Exception) {
            Timber.e(e)
        }
        return unenrollStudentStatus
    }

    suspend fun getCreator() : GetCreatorStatus {
        var getCreatorStatus = GetCreatorStatus.FAIL
        try {
            val response = api().getUser(creator.value!!)
            if (response.isSuccessful) {
                getUserResponse.value = response.body()
                getCreatorStatus = GetCreatorStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return getCreatorStatus
    }
}