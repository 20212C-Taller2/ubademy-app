package com.fiuba.ubademy.network.model

import com.squareup.moshi.Json

data class UserSubscription(
    @Json(name = "subscription_code")
    val subscription: SubscriptionCode,
    @Json(name = "courses_limit")
    val coursesLimit: Int,
    @Json(name = "courses_used")
    val coursesUsed: Int
)