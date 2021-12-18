package com.fiuba.ubademy.network.model

import com.squareup.moshi.Json

data class Subscription(
    val code: SubscriptionCode,
    val description: String,
    val price: Double,
    @Json(name = "course_limit")
    val courseLimit: Int
)