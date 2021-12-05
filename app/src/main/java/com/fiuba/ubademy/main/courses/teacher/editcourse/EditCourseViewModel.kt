package com.fiuba.ubademy.main.courses.teacher.editcourse

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.network.model.EditCourseRequest
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getPlaceName
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class EditCourseViewModel(application: Application) : AndroidViewModel(application) {
    var id = MutableLiveData<Int>()
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var selectedCourseType = MutableLiveData<String>()
    var selectedSubscription = MutableLiveData<String>()

    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String?>()

    var selectedImageUris = MutableLiveData<List<Uri>>()
    var selectedImageFirebasePaths = MutableLiveData<List<String>>()

    var courseTypes = MutableLiveData<Array<String>>()

    init {
        selectedImageFirebasePaths.value = listOf()
    }

    suspend fun getCourseTypes() {
        val response = api().getCourseTypes()
        if (response.isSuccessful) {
            courseTypes.value = response.body()!!.toTypedArray()
        } else {
            throw Exception("Unable to fetch course types.")
        }
    }

    suspend fun fillPlaceName() {
        placeName.value = getPlaceName(placeId.value)
    }

    suspend fun editCourse() : Pair<EditCourseStatus, Course?> {
        var editCourseStatus = EditCourseStatus.FAIL
        var course: Course? = null

        try {
            val response = api().editCourse(id.value!!,
                EditCourseRequest(
                    title = title.value!!,
                    description = description.value!!,
                    type = selectedCourseType.value!!,
                    media = selectedImageFirebasePaths.value!!,
                    creator = getSharedPreferencesData().id,
                    location = placeId.value
                )
            )
            if (response.isSuccessful) {
                editCourseStatus = EditCourseStatus.SUCCESS
                course = response.body()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return Pair(editCourseStatus, course)
    }
}