package com.fiuba.ubademy.main.courses.takenexams.review

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.TakenExam

class ReviewTakenExamViewModel(application: Application) : AndroidViewModel(application) {

    var takenExam = MutableLiveData<TakenExam>()
}