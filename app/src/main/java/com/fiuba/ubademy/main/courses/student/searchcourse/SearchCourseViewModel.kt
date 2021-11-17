package com.fiuba.ubademy.main.courses.student.searchcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.utils.coursesApi

class SearchCourseViewModel(application: Application) : AndroidViewModel(application) {

    var selectedCourseType = MutableLiveData<String>()
    var courseTypes = MutableLiveData<Array<String>>()

    var courses = MutableLiveData<List<Course>>()

    init {
        selectedCourseType.value = ""
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
}