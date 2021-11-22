package com.fiuba.ubademy.main.courses.student.viewcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.UnenrollCourseRequest
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class ViewCourseViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var courseType = MutableLiveData<String>()
    var creator = MutableLiveData<String>()

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
}