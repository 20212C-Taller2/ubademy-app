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
    var basicUserSubscriptionStatus = MutableLiveData<UserSubscriptionStatus>()
    var fullUserSubscriptionStatus = MutableLiveData<UserSubscriptionStatus>()

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

    suspend fun getUserFinancialInformation() {
        val userFinancialInformation = UserFinancialInformation(
            balance = 25.50,
            wallet = "foaghefuog3a08d47gw4apor6o83",
            userSubscriptionStatuses = listOf(
                UserSubscriptionStatus(
                    subscription = SubscriptionCode.BASIC,
                    coursesTotal = 15,
                    coursesUsed = 13),
                UserSubscriptionStatus(
                    subscription = SubscriptionCode.FULL,
                    coursesTotal = 7,
                    coursesUsed = 7),
            )
        )
        balance.value = userFinancialInformation.balance
        wallet.value = userFinancialInformation.wallet
        basicUserSubscriptionStatus.value = userFinancialInformation.userSubscriptionStatuses.first { s -> s.subscription == SubscriptionCode.BASIC }
        fullUserSubscriptionStatus.value = userFinancialInformation.userSubscriptionStatuses.first { s -> s.subscription == SubscriptionCode.FULL }

        // TODO:
//        val response = api().getUserFinancialInformation(userId)
//        if (response.isSuccessful) {
//            balance.value = response.body()!!.balance
//            currentSubscription.value = response.body()!!.subscriptionCode
//        } else {
//            throw Exception("Unable to fetch subscriptions.")
//        }
    }

    suspend fun subscribe(subscriptionCode: SubscriptionCode) : SubscribeStatus {
        var subscribeStatus = SubscribeStatus.FAIL
        try {
            val response = api().subscribe(SubscribeRequest(
                subscription = subscriptionCode,
                userId = userId
            ))
            if (response.isSuccessful)
                subscribeStatus = SubscribeStatus.SUCCESS
        } catch (e: Exception) {
            Timber.e(e)
        }
        return subscribeStatus
    }
}
