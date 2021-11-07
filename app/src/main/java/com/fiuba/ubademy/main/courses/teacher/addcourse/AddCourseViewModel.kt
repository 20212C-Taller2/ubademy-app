package com.fiuba.ubademy.main.courses.teacher.addcourse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.auth.createaccount.CreateAccountStatus
import com.fiuba.ubademy.network.model.AddCourseRequest
import com.fiuba.ubademy.network.model.CreateAccountRequest
import com.fiuba.ubademy.utils.coursesApi
import com.fiuba.ubademy.utils.usersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AddCourseViewModel(application: Application) : AndroidViewModel(application) {
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()

    suspend fun addCourse() : AddCourseStatus {
        var addCourseStatus = AddCourseStatus.FAIL

        withContext(Dispatchers.IO) {
            try {
                val response = coursesApi().addCourse(
                    AddCourseRequest(
                        title = title.value.toString(),
                        description = description.value.toString()
                    )
                )
                if (response.isSuccessful) {
                    addCourseStatus = AddCourseStatus.SUCCESS
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return addCourseStatus
    }
}