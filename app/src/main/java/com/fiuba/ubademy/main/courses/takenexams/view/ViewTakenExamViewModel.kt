package com.fiuba.ubademy.main.courses.takenexams.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fiuba.ubademy.main.courses.TakenExam
import com.fiuba.ubademy.main.courses.GetUserStatus
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class ViewTakenExamViewModel(application: Application) : AndroidViewModel(application) {

    var takenExam = MutableLiveData<TakenExam>()

    var grade = Transformations.map(takenExam) {
        it.examSubmission?.examReview?.grade?.toString() ?: "-"
    }

    var getStudentUserResponse = MutableLiveData<GetUserResponse>()
    var getReviewerUserResponse = MutableLiveData<GetUserResponse>()

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

    suspend fun getReviewer() : GetUserStatus {
        var getUserStatus = GetUserStatus.FAIL
        try {
            val response = api().getUser(takenExam.value!!.examSubmission!!.examReview!!.reviewerId)
            if (response.isSuccessful) {
                getReviewerUserResponse.value = response.body()
                getUserStatus = GetUserStatus.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return getUserStatus
    }
}