package com.fiuba.ubademy.main.profile.subscription

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiuba.ubademy.network.model.*
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import timber.log.Timber

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    var balance = MutableLiveData<Double>()
    var wallet = MutableLiveData<String>()
    var basicUserSubscription = MutableLiveData<UserSubscription>()
    var fullUserSubscription = MutableLiveData<UserSubscription>()

    var freeSubscription = MutableLiveData<Subscription>()
    var basicSubscription = MutableLiveData<Subscription>()
    var fullSubscription = MutableLiveData<Subscription>()

    private val userId = getSharedPreferencesData().id

    suspend fun getSubscriptions() {
        val response = api().getSubscriptions()
        if (response.isSuccessful) {
            val subscriptions = response.body()!!
            freeSubscription.value = subscriptions.first { s -> s.code == SubscriptionCode.FREE }
            basicSubscription.value = subscriptions.first { s -> s.code == SubscriptionCode.BASIC }
            fullSubscription.value = subscriptions.first { s -> s.code == SubscriptionCode.FULL }
        } else {
            throw Exception("Unable to fetch subscriptions.")
        }
    }

    suspend fun getSubscriber() {
        val response = api().getSubscriber(userId)
        if (response.isSuccessful) {
            wallet.value = response.body()!!.wallet
            balance.value = response.body()!!.balance
            basicUserSubscription.value = response.body()!!.subscriptions
                .firstOrNull { s -> s.subscription == SubscriptionCode.BASIC } ?: UserSubscription(
                    subscription = SubscriptionCode.BASIC,
                    coursesLimit = 0,
                    coursesUsed = 0)
            fullUserSubscription.value = response.body()!!.subscriptions
                .firstOrNull { s -> s.subscription == SubscriptionCode.FULL } ?: UserSubscription(
                    subscription = SubscriptionCode.FULL,
                    coursesLimit = 0,
                    coursesUsed = 0)
        } else {
            throw Exception("Unable to fetch subscriber information.")
        }
    }

    suspend fun subscribe(subscriptionCode: SubscriptionCode) : SubscribeStatus {
        var subscribeStatus = SubscribeStatus.FAIL
        try {
            val response = api().subscribe(userId, SubscribeRequest(
                subscription = subscriptionCode
            ))
            if (response.isSuccessful)
                subscribeStatus = SubscribeStatus.SUCCESS
        } catch (e: Exception) {
            Timber.e(e)
        }
        return subscribeStatus
    }
}
