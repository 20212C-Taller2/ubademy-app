package com.fiuba.ubademy.main

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.utils.getDisplayName
import com.fiuba.ubademy.utils.getSharedPreferencesData
import com.fiuba.ubademy.utils.hideKeyboard

class MainDrawerListener : DrawerLayout.DrawerListener {
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        drawerView.requestFocus()
        drawerView.hideKeyboard()

        val sharedPreferencesData = drawerView.context.getSharedPreferencesData()

        displayName.value = sharedPreferencesData.getDisplayName()
        picture.value = sharedPreferencesData.picture
        placeName.value = if (sharedPreferencesData.placeName.isNullOrBlank()) "-" else sharedPreferencesData.placeName
        email.value = sharedPreferencesData.email
    }
    override fun onDrawerOpened(drawerView: View) { }
    override fun onDrawerClosed(drawerView: View) { }
    override fun onDrawerStateChanged(newState: Int) { }

    companion object MainDrawerViewModel {
        var displayName = MutableLiveData<String>()
        var picture = MutableLiveData<String?>()
        var placeName = MutableLiveData<String>()
        var email = MutableLiveData<String>()
    }
}