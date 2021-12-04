package com.fiuba.ubademy.network.model

data class UserSubscriptionStatus(
    val subscription: SubscriptionCode,
    val coursesTotal: Int,
    val coursesUsed: Int
)