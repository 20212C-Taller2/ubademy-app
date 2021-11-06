package com.fiuba.ubademy.main.courses.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.Course

class TeacherCoursesViewModel(application: Application) : AndroidViewModel(application) {

    var courses = MutableLiveData<MutableList<Course>>()

    init {
        val list = mutableListOf<Course>()
        list.add(Course("1", "Test 1", "Test 1"))
        list.add(Course("2", "Test 2", "Test 2"))
        list.add(Course("3", "Test 3", "Test 3"))
        list.add(Course("4", "Test 4", "Test 4"))
        list.add(Course("5", "Test 5", "Test 5"))
        list.add(Course("6", "Test 6", "Test 6"))
        list.add(Course("7", "Test 7", "Test 7"))
        list.add(Course("8", "Test 8", "Test 8"))
        list.add(Course("9", "Test 9", "Test 9"))
        list.add(Course("10", "Test 10", "Test 10"))
        list.add(Course("11", "Test 11", "Test 11"))
        list.add(Course("12", "Test 12", "Test 12"))
        list.add(Course("13", "Test 13", "Test 13"))
        list.add(Course("14", "Test 14", "Test 14"))
        list.add(Course("15", "Test 15", "Test 15"))
        list.add(Course("16", "Test 16", "Test 16"))
        list.add(Course("17", "Test 17", "Test 17"))
        list.add(Course("18", "Test 18", "Test 18"))
        list.add(Course("19", "Test 19", "Test 19"))
        courses.value = list
    }
}