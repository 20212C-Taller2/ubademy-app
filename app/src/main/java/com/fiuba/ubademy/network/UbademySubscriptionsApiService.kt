package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.Subscription
import retrofit2.Response
import retrofit2.http.GET

interface UbademySubscriptionsApiService {
    @GET("subscriptions")
    suspend fun getSubscriptions()
        : Response<List<Subscription>>
}