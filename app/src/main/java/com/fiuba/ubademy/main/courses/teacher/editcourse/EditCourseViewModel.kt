package com.fiuba.ubademy.main.courses.teacher.editcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class EditCourseViewModel(application: Application) : AndroidViewModel(application) {
    var title = MutableLiveData<String>()
}