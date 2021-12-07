package com.fiuba.ubademy.network.model

data class UserSubscription(
    val subscription: SubscriptionCode,
    val coursesTotal: Int,
    val coursesUsed: Int
)