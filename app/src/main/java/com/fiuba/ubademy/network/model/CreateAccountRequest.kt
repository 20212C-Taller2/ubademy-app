package com.fiuba.ubademy.network.model

data class CreateAccountRequest(
    val firstName: String,
    val lastName: String,
    val placeId: String?,
    val email: String,
    val password: String,
    val interests: Set<String>
)