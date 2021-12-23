package com.fiuba.ubademy.main.courses.teacher.exams.addexam

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.Question
import com.fiuba.ubademy.network.model.UpsertExamRequest
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class AddExamViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var title = MutableLiveData<String>()

    suspend fun addExam(questions: List<Question>, published: Boolean) : AddExamStatus {
        var addExamStatus = AddExamStatus.FAIL

        try {
            val response = api().addExam(courseId.value!!,
                UpsertExamRequest(
                    title = title.value!!,
                    published = published,
                    questions = questions
                )
            )
            if (response.isSuccessful) {
                addExamStatus = AddExamStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return addExamStatus
    }
}