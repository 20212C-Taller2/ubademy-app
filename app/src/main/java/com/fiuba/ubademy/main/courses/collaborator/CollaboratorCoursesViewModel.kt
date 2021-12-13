package com.fiuba.ubademy.main.courses.collaborator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class CollaboratorCoursesViewModel(application: Application) : AndroidViewModel(application) {

    var courses = MutableLiveData<List<Course>>()

    private val userId = getSharedPreferencesData().id

    init {
        courses.value = listOf()
    }

    suspend fun getCourses() : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        try {
            val response = api().getCollaboratorCourses(userId,0, 20)
            if (response.isSuccessful) {
                courses.postValue(response.body()!!)
                getCoursesStatus = GetCoursesStatus.SUCCESS
            } else if (response.code() == 404) {
                courses.postValue(listOf())
                getCoursesStatus = GetCoursesStatus.NOT_FOUND
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getCoursesStatus
    }

    suspend fun addCourses(skip: Int) : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        try {
            val response = api().getCollaboratorCourses(userId, skip, 10)
            if (response.isSuccessful) {
                courses.postValue(courses.value!!.plus(response.body()!!))
                getCoursesStatus = GetCoursesStatus.SUCCESS
            } else if (response.code() == 404) {
                getCoursesStatus = GetCoursesStatus.NOT_FOUND
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getCoursesStatus
    }
}