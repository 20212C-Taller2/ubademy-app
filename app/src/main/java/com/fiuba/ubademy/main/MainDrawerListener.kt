package com.fiuba.ubademy.main

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.utils.getSharedPreferencesData
import com.fiuba.ubademy.utils.hideKeyboard

class MainDrawerListener : DrawerLayout.DrawerListener {
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        drawerView.requestFocus()
        drawerView.hideKeyboard()

        val sharedPreferencesData = drawerView.context.getSharedPreferencesData()
        firstName.value = sharedPreferencesData.firstName
        lastName.value = sharedPreferencesData.lastName
        placeName.value = if (sharedPreferencesData.placeName.isNullOrBlank()) "-" else sharedPreferencesData.placeName
        email.value = sharedPreferencesData.email
    }
    override fun onDrawerOpened(drawerView: View) { }
    override fun onDrawerClosed(drawerView: View) { }
    override fun onDrawerStateChanged(newState: Int) { }

    companion object MainDrawerViewModel {
        var firstName = MutableLiveData<String>()
        var lastName = MutableLiveData<String>()
        var placeName = MutableLiveData<String>()
        var email = MutableLiveData<String>()
    }
}