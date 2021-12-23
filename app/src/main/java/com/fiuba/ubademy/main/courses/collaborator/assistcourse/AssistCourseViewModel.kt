package com.fiuba.ubademy.main.courses.collaborator.assistcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetUserStatus
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class AssistCourseViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var courseType = MutableLiveData<String>()
    var subscription = MutableLiveData<String>()
    var creator = MutableLiveData<String>()
    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String>()

    var getUserResponse = MutableLiveData<GetUserResponse>()

    suspend fun getCreator() : GetUserStatus {
        var getUserStatus = GetUserStatus.FAIL
        try {
            val response = api().getUser(creator.value!!)
            if (response.isSuccessful) {
                getUserResponse.value = response.body()
                getUserStatus = GetUserStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return getUserStatus
    }
}