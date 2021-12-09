package com.fiuba.ubademy.main.courses.student.exams.takeexam

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.*
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class TakeExamViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var exam = MutableLiveData<Exam>()

    suspend fun submitExam(answers: List<Answer>) : SubmitExamStatus {
        var submitExamStatus = SubmitExamStatus.FAIL

        try {
            val response = api().submitExam(
                courseId.value!!,
                exam.value!!.id,
                SubmitExamRequest(
                    student = getSharedPreferencesData().id,
                    answers = answers
                )
            )
            if (response.isSuccessful) {
                submitExamStatus = SubmitExamStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return submitExamStatus
    }
}