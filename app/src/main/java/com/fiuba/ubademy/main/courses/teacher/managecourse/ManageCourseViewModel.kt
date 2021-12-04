package com.fiuba.ubademy.main.courses.teacher.managecourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ManageCourseViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var courseType = MutableLiveData<String>()
    var subscription = MutableLiveData<String>()
    var placeId = MutableLiveData<String?>()
    var placeName = MutableLiveData<String>()
}