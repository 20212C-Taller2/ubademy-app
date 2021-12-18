package com.fiuba.ubademy.network.model

import com.squareup.moshi.Json

data class Subscriber(
    @Json(name = "subscriber_id")
    val userId: String,
    val balance: Double,
    @Json(name = "address")
    val wallet: String,
    val subscriptions: List<UserSubscription>
)
