package com.fiuba.ubademy.main.courses.teacher.addcollaborator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fiuba.ubademy.main.courses.GetUsersStatus
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.utils.api
import timber.log.Timber

class AddCollaboratorViewModel(application: Application) : AndroidViewModel(application) {

    var users = MutableLiveData<List<GetUserResponse>>()
    var courseId = MutableLiveData<Int>()
    var collaboratorIds = MutableLiveData<List<String>>()
    var filteredUsers = Transformations.switchMap(users) { users ->
        Transformations.map(collaboratorIds) { collaboratorIds ->
            users.filter { u -> !collaboratorIds.contains(u.id) }
        }
    }

    private var offset = MutableLiveData(0)
    private var limit = 10

    init {
        users.value = listOf()
        collaboratorIds.value = listOf()
    }

    suspend fun getUsers() : GetUsersStatus {
        var getUsersStatus = GetUsersStatus.FAIL

        try {
            val response = api().getUsers(offset = offset.value!!, limit = limit)
            if (response.isSuccessful) {
                getUsersStatus = if (response.body()!!.users.any()) {
                    offset.value = offset.value!! + 1
                    users.postValue(response.body()!!.users)
                    GetUsersStatus.SUCCESS
                } else {
                    users.postValue(listOf())
                    GetUsersStatus.NOT_FOUND
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getUsersStatus
    }

    suspend fun addUsers() : GetUsersStatus {
        var getUsersStatus = GetUsersStatus.FAIL

        try {
            val response = api().getUsers(offset = offset.value!!, limit = limit)
            if (response.isSuccessful) {
                getUsersStatus = if (response.body()!!.users.any()) {
                    offset.value = offset.value!! + 1
                    users.postValue(users.value!!.plus(response.body()!!.users))
                    GetUsersStatus.SUCCESS
                } else {
                    GetUsersStatus.NOT_FOUND
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return getUsersStatus
    }

    suspend fun addCollaborator(userId: String) : AddCollaboratorStatus {
        var addCollaboratorStatus = AddCollaboratorStatus.FAIL
        try {
            val response = api().enrollCollaborator(courseId.value!!, userId)
            if (response.isSuccessful)
                addCollaboratorStatus = AddCollaboratorStatus.SUCCESS
        } catch (e: Exception) {
            Timber.e(e)
        }
        return addCollaboratorStatus
    }
}