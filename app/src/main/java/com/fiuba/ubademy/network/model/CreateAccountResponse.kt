package com.fiuba.ubademy.network.model

data class CreateAccountResponse(
    val auth: Boolean,
    val token: String,
    val user: User
)

data class User(
    val firstName: String,
    val lastName: String,
    val email: String
)