package com.fiuba.ubademy.network.model

import com.squareup.moshi.Json

data class SubscribeRequest(
    @Json(name = "subscription_code")
    val subscription: SubscriptionCode
)