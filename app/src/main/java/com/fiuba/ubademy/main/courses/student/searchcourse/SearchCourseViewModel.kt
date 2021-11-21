package com.fiuba.ubademy.main.courses.student.searchcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.network.model.EnrollCourseRequest
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchCourseViewModel(application: Application) : AndroidViewModel(application) {

    var selectedCourseType = MutableLiveData<String>()
    var courseTypes = MutableLiveData<Array<String>>()

    var selectedSubscription = MutableLiveData<String>()
    var subscriptions = MutableLiveData<Array<String>>()

    var courses = MutableLiveData<List<Course>>()

    init {
        selectedCourseType.value = ""
        selectedSubscription.value = ""
        courses.value = listOf()
    }

    suspend fun getCourseTypes() {
        val response = api().getCourseTypes()
        if (response.isSuccessful) {
            courseTypes.value = arrayOf("") + response.body()!!.toTypedArray()
        } else {
            throw Exception("Unable to fetch course types.")
        }
    }

    suspend fun getSubscriptions() {
        /* TODO
        val response = subscriptionsApi().getSubscriptions()
        if (response.isSuccessful) {
            subscriptions.value = arrayOf("") + response.body()!!.map { item -> item.name }.toTypedArray()
        } else {
            throw Exception("Unable to fetch subscriptions.")
        }
        */
        subscriptions.value = arrayOf("", "FREE", "STARTER", "FULL")
    }

    suspend fun getCoursesFiltered() : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                // TODO: val response = api().getCoursesFiltered(selectedCourseType.value!!, selectedSubscription.value!!,0, 20)
                val response = api().getCourses(0, 20)
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

    suspend fun addCoursesFiltered(skip: Int) : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                // TODO: val response = api().getCoursesFiltered(selectedCourseType.value!!, selectedSubscription.value!!, skip, 10)
                val response = api().getCourses(skip, 10)
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

    suspend fun enrollCourse(courseId: Int) : EnrollCourseStatus {
        var enrollCourseStatus = EnrollCourseStatus.FAIL
        try {
            val response = api().enrollCourse(EnrollCourseRequest(
                userId = getSharedPreferencesData().id,
                courseId = courseId
            ))
            if (response.isSuccessful)
                enrollCourseStatus = EnrollCourseStatus.SUCCESS
        } catch (e: Exception) {
            Timber.e(e)
        }
        return enrollCourseStatus
    }
}