package com.fiuba.ubademy.main.courses.teacher.exams

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.main.courses.GetExamsStatus
import com.fiuba.ubademy.network.model.Exam
import com.fiuba.ubademy.network.model.Question

class ExamsViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var exams = MutableLiveData<List<Exam>>()

    init {
        exams.value = listOf()
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
//TODO:
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
}