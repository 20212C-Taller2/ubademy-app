package com.fiuba.ubademy.main.courses.student.searchcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.coursesApi
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
        val response = coursesApi().getCourseTypes()
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
                val response = coursesApi().getCoursesFiltered(selectedCourseType.value!!, selectedSubscription.value!!,0, 20)
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
                val response = coursesApi().getCoursesFiltered(selectedCourseType.value!!, selectedSubscription.value!!, skip, 10)
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