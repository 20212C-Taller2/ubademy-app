package com.fiuba.ubademy.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.SubscriptionCode
import com.fiuba.ubademy.network.model.UserSubscription
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getDisplayName
import com.fiuba.ubademy.utils.getSharedPreferencesData

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var userId = MutableLiveData<String>()
    var displayName = MutableLiveData<String>()
    var picture = MutableLiveData<String?>()
    var placeName = MutableLiveData<String>()
    var email = MutableLiveData<String>()

    var balance = MutableLiveData<Double>()
    var basicUserSubscriptionCoursesLeft = MutableLiveData(0)
    var fullUserSubscriptionCoursesLeft = MutableLiveData(0)

    init {
        refreshFromSharedPreferences()
    }

    fun refreshFromSharedPreferences() {
        val sharedPreferencesData = getSharedPreferencesData()
        userId.value = sharedPreferencesData.id
        displayName.value = sharedPreferencesData.getDisplayName()
        picture.value = sharedPreferencesData.picture
        placeName.value = if (sharedPreferencesData.placeName.isNullOrBlank()) "-" else sharedPreferencesData.placeName
        email.value = sharedPreferencesData.email
    }

    suspend fun getSubscriber() {
        val response = api().getSubscriber(userId.value!!)
        if (response.isSuccessful) {
            balance.value = response.body()!!.balance
            val basicUserSubscription = response.body()!!.subscriptions
                .firstOrNull { s -> s.subscription == SubscriptionCode.BASIC } ?: UserSubscription(
                subscription = SubscriptionCode.BASIC,
                coursesLimit = 0,
                coursesUsed = 0)
            basicUserSubscriptionCoursesLeft.value = basicUserSubscription.coursesLimit - basicUserSubscription.coursesUsed
            val fullUserSubscription = response.body()!!.subscriptions
                .firstOrNull { s -> s.subscription == SubscriptionCode.FULL } ?: UserSubscription(
                subscription = SubscriptionCode.FULL,
                coursesLimit = 0,
                coursesUsed = 0)
            fullUserSubscriptionCoursesLeft.value = fullUserSubscription.coursesLimit - fullUserSubscription.coursesUsed
        } else {
            throw Exception("Unable to fetch subscriber information.")
        }
    }
}