package com.fiuba.ubademy.main.courses.student.viewcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ViewCourseViewModel(application: Application) : AndroidViewModel(application) {
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var courseType = MutableLiveData<String>()
}