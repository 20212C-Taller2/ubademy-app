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
import com.fiuba.ubademy.network.model.ExamReview
import com.fiuba.ubademy.network.model.ExamSubmission
import com.fiuba.ubademy.network.model.Question
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
                    examSubmission = examSubmissions.firstOrNull() { es -> es.examId == exam.id }
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
        getExamsStatus = GetExamsStatus.SUCCESS
        exams.postValue(listOf(
            Exam(
                id = 1,
                title = "Test exam",
                questions = listOf(
                    Question(1, "Pregunta 1"),
                    Question(2, "¿Que tal?"),
                    Question(3, "¿Preguntas?"),
                )
            ),
            Exam(
                id = 2,
                title = "Test exam 2",
                questions = listOf(
                    Question(1, "Bla bla bla 1"),
                    Question(2, "Bla bla bla 2"),
                    Question(3, "Bla bla bla 3"),
                    Question(4, "Bla bla bla 4"),
                    Question(5, "Bla bla bla 5"),
                    Question(6, "Bla bla bla 6"),
                    Question(7, "Bla bla bla 7"),
                    Question(8, "Bla bla bla 8"),
                    Question(9, "Bla bla bla 9"),
                    Question(10, "Bla bla bla 10"),
                    Question(11, "Bla bla bla 11"),
                    Question(12, "Bla bla bla 12"),
                    Question(13, "Bla bla bla 13"),
                    Question(14, "Bla bla bla 14"),
                    Question(15, "Bla bla bla 15"),
                )
            )
        ))
        // TODO:
//        try {
//            val response = api().getExams(courseId.value!!)
//            if (response.isSuccessful) {
//                exams.postValue(response.body()!!)
//                getExamsStatus = GetExamsStatus.SUCCESS
//            } else if (response.code() == 404) {
//                exams.postValue(listOf())
//                getExamsStatus = GetExamsStatus.NOT_FOUND
//            }
//        } catch (e: Exception) {
//            Timber.e(e)
//        }

        return getExamsStatus
    }

    suspend fun getExamSubmissions() : GetExamSubmissionsStatus {
        var getExamSubmissionsStatus = GetExamSubmissionsStatus.FAIL
        getExamSubmissionsStatus = GetExamSubmissionsStatus.SUCCESS
        examSubmissions.postValue(listOf(
            ExamSubmission(
                id = 32,
                examId = 2,
                studentId = userId,
                answers = listOf(),
                examReview = ExamReview(userId, 7)
            )
        ))

        // TODO:
//        try {
//            val response = api().getExamSubmissions(courseId.value!!, userId, null, 0, exams.value!!.count())
//            if (response.isSuccessful) {
//                examSubmissions.postValue(response.body()!!)
//                getExamSubmissionsStatus = GetExamSubmissionsStatus.SUCCESS
//            } else if (response.code() == 404) {
//                examSubmissions.postValue(listOf())
//                getExamSubmissionsStatus = GetExamSubmissionsStatus.NOT_FOUND
//            }
//        } catch (e: Exception) {
//            Timber.e(e)
//        }

        return getExamSubmissionsStatus
    }
}