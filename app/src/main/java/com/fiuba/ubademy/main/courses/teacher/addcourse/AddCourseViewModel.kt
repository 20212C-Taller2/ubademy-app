package com.fiuba.ubademy.main.courses.teacher.addcourse

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.AddCourseRequest
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class AddCourseViewModel(application: Application) : AndroidViewModel(application) {
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var selectedCourseType = MutableLiveData<String>()
    var selectedSubscription = MutableLiveData<String>()

    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String?>()

    var selectedImageUris = MutableLiveData<List<Uri>>()
    var selectedImageFirebasePaths = MutableLiveData<List<String>>()

    var courseTypes = MutableLiveData<Array<String>>()
    var subscriptions = MutableLiveData<Array<String>>()

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

    suspend fun getSubscriptions() {
        val response = api().getSubscriptions()
        if (response.isSuccessful) {
            subscriptions.value = response.body()!!.sortedBy { item -> item.price }.map { item -> item.code }.toTypedArray()
        } else {
            throw Exception("Unable to fetch subscriptions.")
        }
    }

    suspend fun addCourse() : Pair<AddCourseStatus, Course?> {
        var addCourseStatus = AddCourseStatus.FAIL
        var course: Course? = null

        try {
            val response = api().addCourse(
                AddCourseRequest(
                    title = title.value!!,
                    description = description.value!!,
                    type = selectedCourseType.value!!,
                    subscription = selectedSubscription.value!!,
                    media = selectedImageFirebasePaths.value!!,
                    creator = getSharedPreferencesData().id,
                    location = placeId.value
                )
            )
            if (response.isSuccessful) {
                addCourseStatus = AddCourseStatus.SUCCESS
                course = response.body()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return Pair(addCourseStatus, course)
    }
}