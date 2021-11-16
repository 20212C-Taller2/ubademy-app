package com.fiuba.ubademy.main.courses.teacher.addcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.auth.createaccount.CreateAccountStatus
import com.fiuba.ubademy.network.model.AddCourseRequest
import com.fiuba.ubademy.network.model.CreateAccountRequest
import com.fiuba.ubademy.utils.coursesApi
import com.fiuba.ubademy.utils.usersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AddCourseViewModel(application: Application) : AndroidViewModel(application) {
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var selectedCourseType = MutableLiveData<String>()

    var courseTypes = MutableLiveData<Array<String>>()

    suspend fun getCourseTypes() {
        val response = coursesApi().getCourseTypes()
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
                val response = coursesApi().addCourse(
                    AddCourseRequest(
                        title = title.value!!,
                        description = description.value!!,
                        type = selectedCourseType.value!!
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