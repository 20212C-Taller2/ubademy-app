package com.fiuba.ubademy.main.courses.student.exams

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
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class StudentExamsViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var exams = MutableLiveData<List<Exam>>()
    var examSubmissions = MutableLiveData<List<ExamSubmission>>()
    var takenExams: LiveData<List<TakenExam>> = Transformations.switchMap(exams) { exams ->
        Transformations.map(examSubmissions) { examSubmissions ->
            exams.map { exam ->
                TakenExam(
                    examId = exam.id,
                    title = exam.title,
                    questions = exam.questions,
                    examSubmission = examSubmissions.firstOrNull { es -> es.examId == exam.id }
                )
            }
        }
    }

    private val userId = getSharedPreferencesData().id

    init {
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
            val response = api().getExamSubmissions(courseId.value!!, userId, null, 0, exams.value!!.count())
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
}