package com.fiuba.ubademy.main.courses.takenexams.review

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetUserStatus
import com.fiuba.ubademy.main.courses.TakenExam
import com.fiuba.ubademy.network.model.ExamReview
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class ReviewTakenExamViewModel(application: Application) : AndroidViewModel(application) {

    var courseId = MutableLiveData<Int>()
    var takenExam = MutableLiveData<TakenExam>()
    var grade = MutableLiveData<Int>()
    var feedback = MutableLiveData<String>()

    var getStudentUserResponse = MutableLiveData<GetUserResponse>()

    suspend fun getStudent() : GetUserStatus {
        var getUserStatus = GetUserStatus.FAIL
        try {
            val response = api().getUser(takenExam.value!!.examSubmission!!.studentId)
            if (response.isSuccessful) {
                getStudentUserResponse.value = response.body()
                getUserStatus = GetUserStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return getUserStatus
    }

    suspend fun qualifyTakenExam() : QualifyTakenExamStatus {
        var qualifyTakenExamStatus = QualifyTakenExamStatus.FAIL

        try {
            val response = api().reviewExam(
                courseId.value!!,
                takenExam.value!!.examSubmission!!.id,
                ExamReview(
                    reviewerId = getSharedPreferencesData().id,
                    feedback = feedback.value,
                    grade = grade.value!!
                )
            )
            if (response.isSuccessful) {
                qualifyTakenExamStatus = QualifyTakenExamStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return qualifyTakenExamStatus
    }
}