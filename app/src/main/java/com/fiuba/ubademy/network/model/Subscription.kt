package com.fiuba.ubademy.network.model

import com.squareup.moshi.Json

data class Subscription(
    val code: String,
    val description: String,
    val price: Int,
    @Json(name = "course_limit")
    val courseLimit: Int
)