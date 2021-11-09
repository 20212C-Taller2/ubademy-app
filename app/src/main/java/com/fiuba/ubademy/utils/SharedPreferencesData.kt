package com.fiuba.ubademy.utils

data class SharedPreferencesData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val placeId: String?,
    val placeName: String?,
    val email: String,
    val token: String
)