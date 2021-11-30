package com.fiuba.ubademy.main.courses.student.searchcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
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
        val response = api().getSubscriptions()
        if (response.isSuccessful) {
            subscriptions.value = arrayOf("") + response.body()!!.sortedBy { item -> item.price }.map { item -> item.code }.toTypedArray()
        } else {
            throw Exception("Unable to fetch subscriptions.")
        }
    }

    suspend fun getCoursesFiltered() : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

        try {
            val response = api().getCoursesFiltered(
                if (selectedCourseType.value.isNullOrEmpty()) null else selectedCourseType.value!!,
                if (selectedSubscription.value.isNullOrEmpty()) null else selectedSubscription.value!!,
                0,
                20)
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

    suspend fun addCoursesFiltered(skip: Int) : GetCoursesStatus {
        var getCoursesStatus = GetCoursesStatus.FAIL

            try {
                val response = api().getCoursesFiltered(
                    if (selectedCourseType.value.isNullOrEmpty()) null else selectedCourseType.value!!,
                    if (selectedSubscription.value.isNullOrEmpty()) null else selectedSubscription.value!!,
                    skip,
                    10)
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

    suspend fun enrollStudent(courseId: Int) : EnrollStudentStatus {
        var enrollStudentStatus = EnrollStudentStatus.FAIL
        try {
            val response = api().enrollStudent(courseId, getSharedPreferencesData().id)
            if (response.isSuccessful)
                enrollStudentStatus = EnrollStudentStatus.SUCCESS
        } catch (e: Exception) {
            Timber.e(e)
        }
        return enrollStudentStatus
    }
}