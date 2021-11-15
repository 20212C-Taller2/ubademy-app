package com.fiuba.ubademy.main.courses.collaborator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.coursesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CollaboratorCoursesViewModel(application: Application) : AndroidViewModel(application) {

    var courses = MutableLiveData<List<Course>>()

    init {
        courses.value = listOf()
    }

    suspend fun getCourses() : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = coursesApi().getCourses(0, 20)
                if (response.isSuccessful) {
                    courses.postValue(response.body()!!)
                    getCoursesStatus = GetCoursesStatus.SUCCESS
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return getCoursesStatus
    }

    suspend fun addCourses(skip: Int) : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = coursesApi().getCourses(skip, 10)
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