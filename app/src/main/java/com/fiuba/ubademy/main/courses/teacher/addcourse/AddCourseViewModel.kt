package com.fiuba.ubademy.main.courses.teacher.addcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.auth.createaccount.CreateAccountStatus

class AddCourseViewModel(application: Application) : AndroidViewModel(application) {
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()

    suspend fun addCourse() : AddCourseStatus {
        var createAccountStatus = CreateAccountStatus.FAIL
        return AddCourseStatus.SUCCESS
    }
}