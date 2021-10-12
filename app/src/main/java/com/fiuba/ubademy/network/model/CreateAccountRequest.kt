package com.fiuba.ubademy.network.model

data class CreateAccountRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)