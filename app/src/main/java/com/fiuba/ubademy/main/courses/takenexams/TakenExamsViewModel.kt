package com.fiuba.ubademy.main.courses.takenexams

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fiuba.ubademy.main.courses.GetExamSubmissionsStatus
import com.fiuba.ubademy.main.courses.GetExamsStatus
import com.fiuba.ubademy.main.courses.TakenExam
import com.fiuba.ubademy.network.model.Exam
import com.fiuba.ubademy.network.model.ExamSubmission
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class TakenExamsViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var selectedExamId = MutableLiveData<Int?>()
    var exams = MutableLiveData<List<Exam>>()
    var examSubmissions = MutableLiveData<List<ExamSubmission>>()
    var takenExams: LiveData<List<TakenExam>> = Transformations.switchMap(examSubmissions) { examSubmissions ->
        Transformations.map(exams) { exams ->
            examSubmissions.filter { es -> exams.any { e -> es.examId == e.id } }.map { examSubmission ->
                val exam = exams.first { e -> e.id == examSubmission.examId }
                TakenExam(
                    examId = exam.id,
                    title = exam.title,
                    questions = exam.questions,
                    examSubmission = examSubmission
                )
            }
        }
    }

    init {
        selectedExamId.value = null
        exams.value = listOf()
        examSubmissions.value = listOf()
    }

    suspend fun getExams() : GetExamsStatus {
        var getExamsStatus = GetExamsStatus.FAIL

        try {
            val response = api().getExams(courseId.value!!, true)
            if (response.isSuccessful) {
                exams.value = response.body()!!
                getExamsStatus = GetExamsStatus.SUCCESS
            } else if (response.code() == 404) {
                exams.value = listOf()
                getExamsStatus = GetExamsStatus.NOT_FOUND
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getExamsStatus
    }

    suspend fun getExamSubmissions() : GetExamSubmissionsStatus {
        var getExamSubmissionsStatus = GetExamSubmissionsStatus.FAIL

        try {
            val response = api().getExamSubmissions(courseId.value!!, null, selectedExamId.value, 0, 20)
            if (response.isSuccessful) {
                examSubmissions.value = response.body()!!
                getExamSubmissionsStatus = GetExamSubmissionsStatus.SUCCESS
            } else if (response.code() == 404) {
                examSubmissions.value = listOf()
                getExamSubmissionsStatus = GetExamSubmissionsStatus.NOT_FOUND
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getExamSubmissionsStatus
    }

    suspend fun addExamSubmissions(skip: Int) : GetExamSubmissionsStatus {
        var getExamSubmissionsStatus = GetExamSubmissionsStatus.FAIL

        try {
            val response = api().getExamSubmissions(courseId.value!!, null, selectedExamId.value, skip, 10)
            if (response.isSuccessful) {
                examSubmissions.postValue(examSubmissions.value!!.plus(response.body()!!))
                getExamSubmissionsStatus = GetExamSubmissionsStatus.SUCCESS
            } else if (response.code() == 404) {
                getExamSubmissionsStatus = GetExamSubmissionsStatus.NOT_FOUND
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getExamSubmissionsStatus
    }
}