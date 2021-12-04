package com.fiuba.ubademy.network.model

data class UserFinancialInformation(
    val balance: Double,
    val wallet: String,
    val userSubscriptionStatuses: List<UserSubscriptionStatus>
)
