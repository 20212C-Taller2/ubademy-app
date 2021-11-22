package com.fiuba.ubademy.main.courses.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class StudentCoursesViewModel(application: Application) : AndroidViewModel(application) {

    var courses = MutableLiveData<List<Course>>()

    init {
        courses.value = listOf()
    }

    suspend fun getCourses() : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        try {
            val response = api().getCourses(0, 20)
            if (response.isSuccessful) {
                courses.postValue(response.body()!!)
                getCoursesStatus = GetCoursesStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getCoursesStatus
    }

    suspend fun addCourses(skip: Int) : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        try {
            val response = api().getCourses(skip, 10)
            if (response.isSuccessful) {
                courses.postValue(courses.value!!.plus(response.body()!!))
                getCoursesStatus = GetCoursesStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getCoursesStatus
    }
}