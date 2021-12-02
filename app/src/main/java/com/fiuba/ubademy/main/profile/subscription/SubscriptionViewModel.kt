package com.fiuba.ubademy.main.profile.subscription

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.Subscription
import com.fiuba.ubademy.utils.api

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    var balance = MutableLiveData(0.0)
    var subscriptions = MutableLiveData<List<Subscription>>()
    var freeSubscription = MutableLiveData<Subscription>()
    var basicSubscription = MutableLiveData<Subscription>()
    var fullSubscription = MutableLiveData<Subscription>()

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
}
