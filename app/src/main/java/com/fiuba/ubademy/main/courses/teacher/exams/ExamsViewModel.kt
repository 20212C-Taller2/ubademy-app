package com.fiuba.ubademy.main.courses.teacher.exams

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fiuba.ubademy.main.courses.GetExamsStatus
import com.fiuba.ubademy.network.model.Exam
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class ExamsViewModel(application: Application) : AndroidViewModel(application) {
    var courseId = MutableLiveData<Int>()
    var exams = MutableLiveData<List<Exam>>()
    var filteredExams: LiveData<List<Exam>> = Transformations.switchMap(exams) { exams ->
        Transformations.map(published) { published ->
            exams.filter { e -> published == null || e.published == published }
        }
    }

    var published = MutableLiveData<Boolean?>(null)

    init {
        exams.value = listOf()
    }

    suspend fun getExams() : GetExamsStatus {
        var getExamsStatus = GetExamsStatus.FAIL

        try {
            val response = api().getExams(courseId.value!!)
            if (response.isSuccessful) {
                exams.postValue(response.body()!!)
                getExamsStatus = GetExamsStatus.SUCCESS
            } else if (response.code() == 404) {
                exams.postValue(listOf())
                getExamsStatus = GetExamsStatus.NOT_FOUND
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getExamsStatus
    }
}