package com.fiuba.ubademy.main.courses.students

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fiuba.ubademy.main.courses.GetUsersStatus
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class StudentsViewModel(application: Application) : AndroidViewModel(application) {
    var studentIds = MutableLiveData<List<String>>()
    var students = MutableLiveData<List<GetUserResponse>>()
    var filter = MutableLiveData("")
    var filteredStudents = Transformations.switchMap(students) { students ->
        Transformations.map(filter) { filter ->
            students.filter { s -> filter.isNullOrBlank() || s.displayName.contains(filter, true) }
        }
    }

    init {
        students.value = listOf()
        studentIds.value = listOf()
    }

    suspend fun getStudents() : GetUsersStatus {
        var getUsersStatus = GetUsersStatus.FAIL

        if (studentIds.value!!.isEmpty())
            return GetUsersStatus.NOT_FOUND

        try {
            val response = api().getUsersByIds(studentIds.value!!)
            if (response.isSuccessful) {
                getUsersStatus = if (response.body()!!.users.any()) {
                    students.postValue(response.body()!!.users)
                    GetUsersStatus.SUCCESS
                } else {
                    students.postValue(listOf())
                    GetUsersStatus.NOT_FOUND
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getUsersStatus
    }
}