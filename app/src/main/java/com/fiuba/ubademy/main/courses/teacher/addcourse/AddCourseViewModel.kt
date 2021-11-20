package com.fiuba.ubademy.main.courses.teacher.addcourse

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.AddCourseRequest
import com.fiuba.ubademy.utils.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AddCourseViewModel(application: Application) : AndroidViewModel(application) {
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var selectedCourseType = MutableLiveData<String>()

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

    suspend fun addCourse() : AddCourseStatus {
        var addCourseStatus = AddCourseStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = api().addCourse(
                    AddCourseRequest(
                        title = title.value!!,
                        description = description.value!!,
                        type = selectedCourseType.value!!,
                        media = selectedImageFirebasePaths.value!!
                    )
                )
                if (response.isSuccessful) {
                    addCourseStatus = AddCourseStatus.SUCCESS
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return addCourseStatus
    }
}