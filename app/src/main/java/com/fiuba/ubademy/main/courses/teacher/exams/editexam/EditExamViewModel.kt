package com.fiuba.ubademy.main.courses.teacher.exams.editexam

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.UpsertExamRequest
import com.fiuba.ubademy.network.model.Question
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class EditExamViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var examId = MutableLiveData<Int>()
    var title = MutableLiveData<String>()

    suspend fun editExam(questions: List<Question>, published: Boolean) : EditExamStatus {
        var editExamStatus = EditExamStatus.FAIL

        try {
            val response = api().editExam(courseId.value!!, examId.value!!,
                UpsertExamRequest(
                    title = title.value!!,
                    published = published,
                    questions = questions
                )
            )
            if (response.isSuccessful) {
                editExamStatus = EditExamStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return editExamStatus
    }
}