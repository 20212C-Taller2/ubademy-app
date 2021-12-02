package com.fiuba.ubademy.main.profile.subscription

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.Subscription
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    var balance = MutableLiveData(0.0)
    var subscriptions = MutableLiveData<List<Subscription>>()
    var freeSubscription = MutableLiveData<Subscription>()
    var basicSubscription = MutableLiveData<Subscription>()
    var fullSubscription = MutableLiveData<Subscription>()
    var currentSubscription = MutableLiveData<String>()

    private val userId = getSharedPreferencesData().id

    suspend fun getSubscriptions() {
        val response = api().getSubscriptions()
        if (response.isSuccessful) {
            subscriptions.value = response.body()!!.sortedBy { item -> item.price }
            freeSubscription.value = subscriptions.value!![0]
            basicSubscription.value = subscriptions.value!![1]
            fullSubscription.value = subscriptions.value!![2]
        } else {
            throw Exception("Unable to fetch subscriptions.")
        }
    }

    suspend fun getUserSubscription() {
        balance.value = 10.5
        currentSubscription.value = "FREE"
        // TODO:
//        val response = api().getUserSubscription(userId)
//        if (response.isSuccessful) {
//            balance.value = response.body()!!.balance
//            currentSubscription.value = response.body()!!.subscriptionCode
//        } else {
//            throw Exception("Unable to fetch subscriptions.")
//        }
    }
}
