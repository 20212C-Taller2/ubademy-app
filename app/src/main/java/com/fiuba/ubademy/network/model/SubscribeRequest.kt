package com.fiuba.ubademy.network.model

data class SubscribeRequest(
    val userId: String,
    val subscription: SubscriptionCode
)