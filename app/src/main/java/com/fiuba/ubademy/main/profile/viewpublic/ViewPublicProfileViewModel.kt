package com.fiuba.ubademy.main.profile.viewpublic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ViewPublicProfileViewModel(application: Application) : AndroidViewModel(application) {

    var id = MutableLiveData<String>()
    var displayName = MutableLiveData<String>()
    var picture = MutableLiveData<String?>()
}