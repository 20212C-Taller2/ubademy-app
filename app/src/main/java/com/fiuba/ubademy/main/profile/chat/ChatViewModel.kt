package com.fiuba.ubademy.main.profile.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.NotifyRequest
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    var userId = MutableLiveData<String>()
    var userDisplayName = MutableLiveData<String>()
    var message = MutableLiveData<String>()

    var currentUserId = getSharedPreferencesData().id

    suspend fun notify(message: String) {
        try {
            api().notify(NotifyRequest(
                from = currentUserId,
                to = userId.value!!,
                message
            ))
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}