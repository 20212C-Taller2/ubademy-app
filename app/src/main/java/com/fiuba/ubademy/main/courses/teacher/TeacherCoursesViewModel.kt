package com.fiuba.ubademy.main.courses.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.coursesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class TeacherCoursesViewModel(application: Application) : AndroidViewModel(application) {

    var courses = MutableLiveData<List<Course>>()

    init {
        courses.value = listOf()
    }

    suspend fun getCourses(skip: Int = 0, limit: Int = 20) : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = coursesApi().getCourses(skip, limit)
                if (response.isSuccessful) {
                    courses.postValue(courses.value!!.plus(response.body()!!))
                    getCoursesStatus = GetCoursesStatus.SUCCESS
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return getCoursesStatus
    }
}