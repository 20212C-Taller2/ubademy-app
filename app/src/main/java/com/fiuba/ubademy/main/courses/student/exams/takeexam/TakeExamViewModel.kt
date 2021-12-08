package com.fiuba.ubademy.main.courses.student.exams.takeexam

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.Exam

class TakeExamViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var exam = MutableLiveData<Exam>()
}