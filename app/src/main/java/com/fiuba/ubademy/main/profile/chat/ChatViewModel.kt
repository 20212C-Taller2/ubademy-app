package com.fiuba.ubademy.main.profile.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    var userId = MutableLiveData<String>()
    var userDisplayName = MutableLiveData<String>()
    var message = MutableLiveData<String>()
}